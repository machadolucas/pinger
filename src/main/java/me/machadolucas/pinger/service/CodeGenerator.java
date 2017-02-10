package me.machadolucas.diagnosis.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Gerador de códigos para pessoas e para logar nas sessões.
 */
@Component
public class CodeGenerator {

    public CodeGenerator() {
        this.stringBuffer = new char[5];
        this.numericBuffer = new char[4];
    }

    private final char[] stringBuffer;
    private final char[] numericBuffer;

    private static final String stringSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String numericSymbols = "0123456789";

    private static final Random random = new SecureRandom();

    /**
     * Gera códigos para identificar os pacientes
     *
     * @return Uma string literal com 5 caracteres
     */
    public String generateLiteralCode() {
        for (int idx = 0; idx < this.stringBuffer.length; ++idx)
            this.stringBuffer[idx] = stringSymbols.charAt(random.nextInt(stringSymbols.length()));
        return new String(this.stringBuffer);
    }

    /**
     * Gera códigos/senhas para logar nas sessões de questionários
     *
     * @return Uma string numérica com 4 caracteres
     */
    public String generateNumericCode() {
        for (int idx = 0; idx < this.numericBuffer.length; ++idx)
            this.numericBuffer[idx] = numericSymbols.charAt(random.nextInt(numericSymbols.length()));
        return new String(this.numericBuffer);
    }

}
