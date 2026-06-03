package com.bbs.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MD5UtilTest {

    @Test
    public void testEncode() {
        String result = MD5Util.encode("123456");
        assertNotNull(result);
        assertEquals(32, result.length(), "MD5哈希应该是32字符");

        String result2 = MD5Util.encode("123456");
        assertEquals(result, result2);
    }

    @Test
    public void testEncodeDifferentInputs() {
        String hash1 = MD5Util.encode("123456");
        String hash2 = MD5Util.encode("654321");
        assertNotEquals(hash1, hash2, "不同输入应该产生不同哈希");
    }

    @Test
    public void testVerify() {
        String password = "123456";
        String hash = MD5Util.encode(password);

        assertTrue(MD5Util.verify(password, hash), "验证正确密码应该成功");
        assertFalse(MD5Util.verify("wrongpassword", hash), "验证错误密码应该失败");
    }

    @Test
    public void testVerifyEmptyString() {
        String hash = MD5Util.encode("");
        assertNotNull(hash);
        assertTrue(MD5Util.verify("", hash), "验证空字符串应该成功");
    }

    @Test
    public void testVerifyWithKnownHash() {
        // MD5("password") = 5f4dcc3b5aa765d61d8327deb882cf99
        String knownHash = "5f4dcc3b5aa765d61d8327deb882cf99";
        assertTrue(MD5Util.verify("password", knownHash), "验证已知哈希应该成功");
    }

    @Test
    public void testEncodeSpecialCharacters() {
        String result = MD5Util.encode("密码123");
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    @Test
    public void testEncodeLongString() {
        String longString = "a".repeat(1000);
        String result = MD5Util.encode(longString);
        assertNotNull(result);
        assertEquals(32, result.length());
    }
}
