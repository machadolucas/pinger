package me.machadolucas.diagnosis.view;

import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.servlet.http.Cookie;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Essa classe gerencia a inserção e validação de cookies de autenticação Cookies são o nome do usuário logado
 * encriptado, e codificado em base64.
 */
@Component
public class CookieMonster {

    public static final String AUTH_COOKIE_NAME = "sessionAuth";
    public static final String ADMIN_AUTH_COOKIE_NAME = "sessionAdmAuth";

    private static SecretKey myDesKey;
    private static Cipher desCipher;

    public CookieMonster() {
        try {
            final KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
            myDesKey = keygenerator.generateKey();
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public Cookie createSessionCookie(final String username) {
        return createCookie(username, AUTH_COOKIE_NAME);
    }

    public Cookie createAdminCookie(final String username) {
        return createCookie(username, ADMIN_AUTH_COOKIE_NAME);
    }

    private Cookie createCookie(final String username, final String cookieTypeName) {
        try {
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            String encoded = Base64.getUrlEncoder().encodeToString(desCipher.doFinal(username.getBytes()));

            encoded = encoded.replaceAll("=", "@");
            final Cookie myCookie = new Cookie(cookieTypeName, encoded);

            myCookie.setMaxAge(1200); // Make cookie expire in 20 minutes

            return myCookie;

        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decryptCookie(final Cookie cookie) {

        try {
            desCipher.init(Cipher.DECRYPT_MODE, myDesKey);

            return new String(desCipher.doFinal(Base64.getUrlDecoder().decode(cookie.getValue().replaceAll("@", "=")
                    .getBytes())));

        } catch (InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (final BadPaddingException e) {
            System.out.println("Cookie inválido: " + e.getMessage());
        }

        return null;
    }

}
