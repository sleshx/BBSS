package sg.gov.msf.bbss.apputils.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Base64;

/**
 * Created by bandaray on 11/12/2014.
 */
public class Cryptographer {

	private static final String UTF8 = "utf-8";
	private static final char[] SEKRIT = null ; // INSERT A RANDOM PASSWORD HERE.

	private Context context;

	public Cryptographer(Context context) {
		this.context = context;
	}

	public String encrypt(String value) {
		try {
			final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];
			String androidId = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));

			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(androidId.getBytes(UTF8), 20));

			return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP),UTF8);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String decrypt(String value) {
		try {
			final byte[] bytes = value!=null ? Base64.decode(value,Base64.DEFAULT) : new byte[0];
			String androidId = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = keyFactory.generateSecret(new PBEKeySpec(SEKRIT));

			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(androidId.getBytes(UTF8), 20));

			return new String(pbeCipher.doFinal(bytes),UTF8);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;

	}
}
