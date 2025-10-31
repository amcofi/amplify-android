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

package com.amplifyframework.storage.s3.options

import aws.sdk.kotlin.services.s3.model.StorageClass
import com.amplifyframework.storage.s3.ServerSideEncryption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for storage class configuration in upload options.
 */
class AWSS3StorageUploadOptionsStorageClassTest {

    @Test
    fun `AWSS3StorageUploadFileOptions builder sets storage class`() {
        // GIVEN & WHEN
        val options = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.GlacierIr)
            .build()

        // THEN
        assertEquals(StorageClass.GlacierIr, options.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadFileOptions builder defaults to null storage class`() {
        // GIVEN & WHEN
        val options = AWSS3StorageUploadFileOptions.builder()
            .build()

        // THEN
        assertNull(options.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadFileOptions supports different storage classes`() {
        // Test Standard
        val standardOptions = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.Standard)
            .build()
        assertEquals(StorageClass.Standard, standardOptions.storageClass)

        // Test Glacier
        val glacierOptions = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.Glacier)
            .build()
        assertEquals(StorageClass.Glacier, glacierOptions.storageClass)

        // Test Deep Archive
        val deepArchiveOptions = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.DeepArchive)
            .build()
        assertEquals(StorageClass.DeepArchive, deepArchiveOptions.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadFileOptions from copies storage class`() {
        // GIVEN
        val originalOptions = AWSS3StorageUploadFileOptions.builder()
            .contentType("image/jpeg")
            .storageClass(StorageClass.GlacierIr)
            .serverSideEncryption(ServerSideEncryption.AES256)
            .build()

        // WHEN
        val copiedOptions = AWSS3StorageUploadFileOptions.from(originalOptions)
            .build()

        // THEN
        assertEquals(StorageClass.GlacierIr, copiedOptions.storageClass)
        assertEquals("image/jpeg", copiedOptions.contentType)
        assertEquals(ServerSideEncryption.AES256, copiedOptions.serverSideEncryption)
    }

    @Test
    fun `AWSS3StorageUploadInputStreamOptions builder sets storage class`() {
        // GIVEN & WHEN
        val options = AWSS3StorageUploadInputStreamOptions.builder()
            .storageClass(StorageClass.IntelligentTiering)
            .build()

        // THEN
        assertEquals(StorageClass.IntelligentTiering, options.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadInputStreamOptions builder defaults to null storage class`() {
        // GIVEN & WHEN
        val options = AWSS3StorageUploadInputStreamOptions.builder()
            .build()

        // THEN
        assertNull(options.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadInputStreamOptions supports different storage classes`() {
        // Test Standard-IA
        val standardIAOptions = AWSS3StorageUploadInputStreamOptions.builder()
            .storageClass(StorageClass.StandardIa)
            .build()
        assertEquals(StorageClass.StandardIa, standardIAOptions.storageClass)

        // Test One Zone-IA
        val oneZoneIAOptions = AWSS3StorageUploadInputStreamOptions.builder()
            .storageClass(StorageClass.OnezoneIa)
            .build()
        assertEquals(StorageClass.OnezoneIa, oneZoneIAOptions.storageClass)
    }

    @Test
    fun `AWSS3StorageUploadInputStreamOptions from copies storage class`() {
        // GIVEN
        val originalOptions = AWSS3StorageUploadInputStreamOptions.builder()
            .contentType("application/json")
            .storageClass(StorageClass.Glacier)
            .serverSideEncryption(ServerSideEncryption.NONE)
            .build()

        // WHEN
        val copiedOptions = AWSS3StorageUploadInputStreamOptions.from(originalOptions)
            .build()

        // THEN
        assertEquals(StorageClass.Glacier, copiedOptions.storageClass)
        assertEquals("application/json", copiedOptions.contentType)
        assertEquals(ServerSideEncryption.NONE, copiedOptions.serverSideEncryption)
    }

    @Test
    fun `AWSS3StorageUploadFileOptions equals and hashCode include storage class`() {
        // GIVEN
        val options1 = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.GlacierIr)
            .build()

        val options2 = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.GlacierIr)
            .build()

        val options3 = AWSS3StorageUploadFileOptions.builder()
            .storageClass(StorageClass.Standard)
            .build()

        // THEN
        assertEquals(options1, options2)
        assertEquals(options1.hashCode(), options2.hashCode())
        assert(options1 != options3)
    }

    @Test
    fun `AWSS3StorageUploadInputStreamOptions equals and hashCode include storage class`() {
        // GIVEN
        val options1 = AWSS3StorageUploadInputStreamOptions.builder()
            .storageClass(StorageClass.Glacier)
            .build()

        val options2 = AWSS3StorageUploadInputStreamOptions.builder()
            .storageClass(StorageClass.Glacier)
            .build()

        val options3 = AWSS3StorageUploadInputStreamOptions.builder()
            .storageClass(StorageClass.Standard)
            .build()

        // THEN
        assertEquals(options1, options2)
        assertEquals(options1.hashCode(), options2.hashCode())
        assert(options1 != options3)
    }
}
