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

package com.amplifyframework.storage.s3.request

import aws.sdk.kotlin.services.s3.model.StorageClass
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.s3.ServerSideEncryption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for storage class configuration in upload requests.
 */
@Suppress("DEPRECATION")
class AWSS3StorageUploadRequestStorageClassTest {

    @Test
    fun `AWSS3StorageUploadRequest with 9 parameters sets storage class`() {
        // GIVEN & WHEN
        val request = AWSS3StorageUploadRequest<String>(
            "test-key",
            "test-data",
            StorageAccessLevel.PUBLIC,
            null,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false,
            StorageClass.GlacierIr
        )

        // THEN
        assertEquals(StorageClass.GlacierIr, request.storageClass)
        assertEquals("test-key", request.key)
        assertEquals("test-data", request.local)
    }

    @Test
    fun `AWSS3StorageUploadRequest with 8 parameters defaults to null storage class`() {
        // GIVEN & WHEN - Using backward compatible constructor
        val request = AWSS3StorageUploadRequest<String>(
            "test-key",
            "test-data",
            StorageAccessLevel.PUBLIC,
            null,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false
        )

        // THEN
        assertNull(request.storageClass)
        assertEquals("test-key", request.key)
        assertEquals("test-data", request.local)
    }

    @Test
    fun `AWSS3StorageUploadRequest supports different storage classes`() {
        // Test Standard
        val standardRequest = AWSS3StorageUploadRequest<String>(
            "key1",
            "data1",
            StorageAccessLevel.PUBLIC,
            null,
            "text/plain",
            ServerSideEncryption.NONE,
            emptyMap(),
            false,
            StorageClass.Standard
        )
        assertEquals(StorageClass.Standard, standardRequest.storageClass)

        // Test Glacier
        val glacierRequest = AWSS3StorageUploadRequest<String>(
            "key2",
            "data2",
            StorageAccessLevel.PROTECTED,
            "user123",
            "application/json",
            ServerSideEncryption.AES256,
            mapOf("custom" to "metadata"),
            true,
            StorageClass.Glacier
        )
        assertEquals(StorageClass.Glacier, glacierRequest.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadRequest preserves other parameters when storage class is set`() {
        // GIVEN & WHEN
        val metadata = mapOf("key1" to "value1", "key2" to "value2")
        val request = AWSS3StorageUploadRequest<ByteArray>(
            "my-file.bin",
            byteArrayOf(1, 2, 3),
            StorageAccessLevel.PRIVATE,
            "user-456",
            "application/octet-stream",
            ServerSideEncryption.AES256,
            metadata,
            true,
            StorageClass.DeepArchive
        )

        // THEN
        assertEquals("my-file.bin", request.key)
        assertEquals(StorageAccessLevel.PRIVATE, request.accessLevel)
        assertEquals("user-456", request.targetIdentityId)
        assertEquals("application/octet-stream", request.contentType)
        assertEquals(ServerSideEncryption.AES256, request.serverSideEncryption)
        assertEquals(metadata, request.metadata)
        assertEquals(true, request.useAccelerateEndpoint())
        assertEquals(StorageClass.DeepArchive, request.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadRequest backward compatibility with old constructor`() {
        // GIVEN & WHEN - This is how existing code would call it
        val metadata = mapOf("old" to "style")
        val request = AWSS3StorageUploadRequest<String>(
            "legacy-key",
            "legacy-data",
            StorageAccessLevel.PUBLIC,
            null,
            "text/html",
            ServerSideEncryption.NONE,
            metadata,
            false
        )

        // THEN - All old fields work
        assertEquals("legacy-key", request.key)
        assertEquals("legacy-data", request.local)
        assertEquals(StorageAccessLevel.PUBLIC, request.accessLevel)
        assertNull(request.targetIdentityId)
        assertEquals("text/html", request.contentType)
        assertEquals(ServerSideEncryption.NONE, request.serverSideEncryption)
        assertEquals(metadata, request.metadata)
        assertEquals(false, request.useAccelerateEndpoint())
        
        // Storage class should be null by default
        assertNull(request.storageClass)
    }
}
