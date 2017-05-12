package com.attendance.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

public class AESUtil {
	
	private static final byte[] keyBytes = {0x47,0x75,0x61,0x6E,0x67,0x5A,0x68,0x6F,0x75,0x47,0x41,0x53,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20};
	private static final byte[] ivBytes = {0x4E,0x65,0x77,0x61,0x63,0x65,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20,0x20};
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM_CBC_PADDING5 = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF-8";
	
	/**加密
	 * @param oldStr
	 * @return
	 */
	@SuppressLint("TrulyRandom")
	public static String encryptWithBase64(String oldStr) {
		try {
			if(null != oldStr && oldStr.length() > 0) {
				Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC_PADDING5);
				cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM), new IvParameterSpec(ivBytes));
				return Base64.encodeToString(cipher.doFinal(oldStr.getBytes(CHARSET)), Base64.DEFAULT);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**解密
	 * @param base64String
	 * @return
	 */
	public static String decryptWithBase64(String base64String) {
		try {
			if(null != base64String && base64String.length() > 0) {
				Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC_PADDING5);
				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, KEY_ALGORITHM), new IvParameterSpec(ivBytes));
				return new String(cipher.doFinal(Base64.decode(base64String.getBytes(CHARSET), Base64.DEFAULT)), CHARSET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	 
}
