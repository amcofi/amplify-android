/*
 * Copyright 2024 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amplifyframework.storage.s3.operation

import aws.sdk.kotlin.services.s3.model.StorageClass
import com.amplifyframework.auth.AuthCredentialsProvider
import com.amplifyframework.core.Consumer
import com.amplifyframework.storage.ObjectMetadata
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.StoragePath
import com.amplifyframework.storage.s3.ServerSideEncryption
import com.amplifyframework.storage.s3.request.AWSS3StoragePathUploadRequest
import com.amplifyframework.storage.s3.service.StorageService
import com.google.common.util.concurrent.MoreExecutors
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for storage class functionality in upload operations.
 */
class AWSS3StorageUploadOperationStorageClassTest {

    private lateinit var storageService: StorageService
    private lateinit var authCredentialsProvider: AuthCredentialsProvider

    @Before
    fun setup() {
        storageService = mockk<StorageService>(relaxed = true)
        authCredentialsProvider = mockk(relaxed = true)
    }

    @Test
    fun `AWSS3StoragePathUploadFileOperation uses GlacierIr storage class when specified`() {
        // GIVEN
        val path = StoragePath.fromString("public/test.txt")
        val tempFile = File.createTempFile("test", ".txt")
        val request = AWSS3StoragePathUploadRequest(
            path,
            tempFile,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false,
            StorageClass.GlacierIr
        )
        val onError = mockk<Consumer<StorageException>>(relaxed = true)
        val operation = AWSS3StoragePathUploadFileOperation(
            request = request,
            storageService = storageService,
            executorService = MoreExecutors.newDirectExecutorService(),
            authCredentialsProvider = authCredentialsProvider,
            {},
            {},
            onError
        )

        // WHEN
        operation.start()

        // THEN
        val metadataSlot = slot<ObjectMetadata>()
        verify {
            storageService.uploadFile(
                any(),
                any(),
                any(),
                capture(metadataSlot),
                any()
            )
        }
        assertEquals(
            StorageClass.GlacierIr.value,
            metadataSlot.captured.metaData[ObjectMetadata.STORAGE_CLASS]
        )
    }

    @Test
    fun `AWSS3StoragePathUploadFileOperation uses Standard storage class when not specified`() {
        // GIVEN
        val path = StoragePath.fromString("public/test.txt")
        val tempFile = File.createTempFile("test", ".txt")
        val request = AWSS3StoragePathUploadRequest(
            path,
            tempFile,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false,
            null // No storage class specified
        )
        val onError = mockk<Consumer<StorageException>>(relaxed = true)
        val operation = AWSS3StoragePathUploadFileOperation(
            request = request,
            storageService = storageService,
            executorService = MoreExecutors.newDirectExecutorService(),
            authCredentialsProvider = authCredentialsProvider,
            {},
            {},
            onError
        )

        // WHEN
        operation.start()

        // THEN
        val metadataSlot = slot<ObjectMetadata>()
        verify {
            storageService.uploadFile(
                any(),
                any(),
                any(),
                capture(metadataSlot),
                any()
            )
        }
        assertEquals(
            StorageClass.Standard.value,
            metadataSlot.captured.metaData[ObjectMetadata.STORAGE_CLASS]
        )
    }

    @Test
    fun `AWSS3StoragePathUploadFileOperation supports different storage classes`() {
        // Test Glacier
        testStorageClassInUploadFileOperation(StorageClass.Glacier)
        
        // Test Deep Archive
        testStorageClassInUploadFileOperation(StorageClass.DeepArchive)
        
        // Test Intelligent Tiering
        testStorageClassInUploadFileOperation(StorageClass.IntelligentTiering)
    }

    private fun testStorageClassInUploadFileOperation(storageClass: StorageClass) {
        val path = StoragePath.fromString("public/test.txt")
        val tempFile = File.createTempFile("test", ".txt")
        val request = AWSS3StoragePathUploadRequest(
            path,
            tempFile,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false,
            storageClass
        )
        val onError = mockk<Consumer<StorageException>>(relaxed = true)
        val operation = AWSS3StoragePathUploadFileOperation(
            request = request,
            storageService = storageService,
            executorService = MoreExecutors.newDirectExecutorService(),
            authCredentialsProvider = authCredentialsProvider,
            {},
            {},
            onError
        )

        operation.start()

        val metadataSlot = slot<ObjectMetadata>()
        verify {
            storageService.uploadFile(
                any(),
                any(),
                any(),
                capture(metadataSlot),
                any()
            )
        }
        assertEquals(
            storageClass.value,
            metadataSlot.captured.metaData[ObjectMetadata.STORAGE_CLASS]
        )
    }

    @Test
    fun `AWSS3StoragePathUploadInputStreamOperation uses specified storage class`() {
        // GIVEN
        val path = StoragePath.fromString("public/data.json")
        val inputStream = File.createTempFile("data", ".json").inputStream()
        val request = AWSS3StoragePathUploadRequest(
            path,
            inputStream,
            "application/json",
            ServerSideEncryption.NONE,
            emptyMap(),
            false,
            StorageClass.StandardIa
        )
        val onError = mockk<Consumer<StorageException>>(relaxed = true)
        val operation = AWSS3StoragePathUploadInputStreamOperation(
            request = request,
            storageService = storageService,
            executorService = MoreExecutors.newDirectExecutorService(),
            authCredentialsProvider = authCredentialsProvider,
            {},
            {},
            onError
        )

        // WHEN
        operation.start()

        // THEN
        val metadataSlot = slot<ObjectMetadata>()
        verify {
            storageService.uploadInputStream(
                any(),
                any(),
                any(),
                capture(metadataSlot),
                any()
            )
        }
        assertEquals(
            StorageClass.StandardIa.value,
            metadataSlot.captured.metaData[ObjectMetadata.STORAGE_CLASS]
        )
    }

    @Test
    fun `AWSS3StoragePathUploadInputStreamOperation defaults to Standard storage class`() {
        // GIVEN
        val path = StoragePath.fromString("public/data.json")
        val inputStream = File.createTempFile("data", ".json").inputStream()
        val request = AWSS3StoragePathUploadRequest(
            path,
            inputStream,
            "application/json",
            ServerSideEncryption.NONE,
            emptyMap(),
            false
            // No storage class specified - should use default parameter
        )
        val onError = mockk<Consumer<StorageException>>(relaxed = true)
        val operation = AWSS3StoragePathUploadInputStreamOperation(
            request = request,
            storageService = storageService,
            executorService = MoreExecutors.newDirectExecutorService(),
            authCredentialsProvider = authCredentialsProvider,
            {},
            {},
            onError
        )

        // WHEN
        operation.start()

        // THEN
        val metadataSlot = slot<ObjectMetadata>()
        verify {
            storageService.uploadInputStream(
                any(),
                any(),
                any(),
                capture(metadataSlot),
                any()
            )
        }
        assertEquals(
            StorageClass.Standard.value,
            metadataSlot.captured.metaData[ObjectMetadata.STORAGE_CLASS]
        )
    }

    @Test
    fun `AWSS3StoragePathUploadRequest supports backward compatible constructor`() {
        // GIVEN & WHEN - Using old 6-parameter constructor
        val path = StoragePath.fromString("public/legacy.txt")
        val tempFile = File.createTempFile("legacy", ".txt")
        val request = AWSS3StoragePathUploadRequest(
            path,
            tempFile,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false
            // No storage class parameter - should default to null
        )

        // THEN
        assertNotNull(request)
        assertEquals(path, request.path)
        assertEquals(tempFile, request.local)
        assertEquals(null, request.storageClass) // Should be null by default
    }

    @Test
    fun `storage class metadata is set alongside other metadata`() {
        // GIVEN
        val path = StoragePath.fromString("public/test.txt")
        val tempFile = File.createTempFile("test", ".txt")
        val customMetadata = mapOf("custom-key" to "custom-value")
        val request = AWSS3StoragePathUploadRequest(
            path,
            tempFile,
            "text/plain",
            ServerSideEncryption.AES256,
            customMetadata,
            false,
            StorageClass.GlacierIr
        )
        val onError = mockk<Consumer<StorageException>>(relaxed = true)
        val operation = AWSS3StoragePathUploadFileOperation(
            request = request,
            storageService = storageService,
            executorService = MoreExecutors.newDirectExecutorService(),
            authCredentialsProvider = authCredentialsProvider,
            {},
            {},
            onError
        )

        // WHEN
        operation.start()

        // THEN
        val metadataSlot = slot<ObjectMetadata>()
        verify {
            storageService.uploadFile(
                any(),
                any(),
                any(),
                capture(metadataSlot),
                any()
            )
        }
        val capturedMetadata = metadataSlot.captured
        
        // Verify storage class is set
        assertEquals(
            StorageClass.GlacierIr.value,
            capturedMetadata.metaData[ObjectMetadata.STORAGE_CLASS]
        )
        
        // Verify other metadata is also set
        assertEquals("text/plain", capturedMetadata.metaData[ObjectMetadata.CONTENT_TYPE])
        assertEquals(
            ServerSideEncryption.AES256.getName(),
            capturedMetadata.metaData[ObjectMetadata.SERVER_SIDE_ENCRYPTION]
        )
        assertEquals(customMetadata, capturedMetadata.userMetadata)
    }
}
