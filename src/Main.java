import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Main {
    private static final ArrayList<String> lines = new ArrayList<>();
    private static final ArrayList<String> labels = new ArrayList<>();

    private static final StringBuilder hexBuffer = new StringBuilder("v2.0 raw\n"); // IO time reducing
    private static final String[] op = new String[]{
            "lsls", "lsrs", "asrs", "add", "sub", "movs", "cmp",
            "ands", "eors", "adcs", "sbcs", "rors", "tst", "rsbs",
            "cmn", "orrs", "muls", "bics", "mvns", "add", "str", "sub", "ldr", "b\t"};

    public static void main(String[] args) throws IOException {
        readProgram();
        // lines.forEach(x -> System.out.println(Arrays.toString(cleanedLine(x))));
        lines.forEach(x -> packetSwitching(cleanedLine(x)));
        System.out.println("---");
        lines.forEach(System.out::println);
        System.out.println("---");
        labels.forEach(System.out::println);

        writeResult(hexBuffer.toString());
    }

    private static String[] cleanedLine(String x) {
        String[] tmp = x.strip().split("\\s+");
        tmp[0] = tmp[0].trim();
        if (tmp[0].contains("LBB")) {
            // tmp[0] = tmp[0].replaceAll(".LBB0_", "").replaceAll(":", "");
            return null;
        } else if (tmp[0].equals("b")) {
            // tmp[1] = tmp[1].trim().replaceAll(".LBB0_", "");
            return null;
        } else {
            if (tmp.length > 1) {
                tmp[1] = tmp[1].replaceAll(",", "");
            }
            if (tmp.length > 2) {
                tmp[2] = tmp[2].trim()
                        .replaceAll("\\[sp, ", "")
                        .replaceAll("]", "")
                        .replaceAll(",", "");
            }
            if (tmp.length > 3) {
                tmp[2] = tmp[2].replaceAll("\\[", "");
                tmp[3] = tmp[3].trim()
                        .replaceAll("\\[sp, ", "")
                        .replaceAll("]", "")
                        .replaceAll(",", "");
            }
        }
        System.out.println(x + " cleaned to " + Arrays.toString(tmp));
        return tmp;
    }

    private static void readProgram() {
        try {
            FileReader reader = new FileReader("code_c/tty.s");

            BufferedReader bufferedReader = new BufferedReader(reader);

            String line; // temp
            while ((line = bufferedReader.readLine()) != null) {
                // Selecting & Filtering ASM lines
                if (Arrays.stream(op).anyMatch(line.trim()::contains)) {
                    lines.add(line);
                }
                if (line.contains("LBB") && !(line.contains("b\t"))) {
                    labels.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeResult(String buffer) {
        final boolean APPEND = false;
        try {
            FileWriter writer = new FileWriter("result.bin", APPEND);
            writer.write(buffer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void packetSwitching(String[] line) {
        if (line == null) return;
        switch (line[0]) {
            case "lsls":
                if (line.length == 4) {
                    System.out.println("Call LSL_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                    LSL_IMMEDIATE(line[1], line[2], line[3]);
                } else {
                    System.out.println("Call LSL_REGISTER with " + line[1] + " | " + line[2]);
                    LSL_REGISTER(line[1], line[2]);
                }
                break;
            case "lsrs":
                if (line.length == 4) {
                    System.out.println("Call LSR_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                    LSR_IMMEDIATE(line[1], line[2], line[3]);
                } else {
                    System.out.println("Call LSL_REGISTER with " + line[1] + " | " + line[2]);
                    LSR_REGISTER(line[1], line[2]);
                }
                break;
            case "asrs":
                if (line.length == 4) {
                    System.out.println("Call ASR_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                    ASR_IMMEDIATE(line[1], line[2], line[3]);
                } else {
                    System.out.println("Call ASR_REGISTER with " + line[1] + " | " + line[2]);
                    ASR_REGISTER(line[1], line[2]);
                }
                break;
            case "adds":
                if (line[2].contains("#")){
                    System.out.println("Call ADD_8_IMMEDIATE with " + line[1] + " | " + line[2]);
                    ADD_8_IMMEDIATE(line[1], line[2]);
                }
                else if (line[3].contains("#")){
                    if ((intToBinary(line[3],0)).length() <= 3) {
                        System.out.println("Call ADD_3_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                        ADD_3_IMMEDIATE(line[1], line[2], line[3]);
                    } else if (Objects.equals(line[1], line[2])){
                        System.out.println("Call ADD_8_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                        ADD_8_IMMEDIATE(line[1], line[3]);
                    }
                } else {
                    System.out.println("Call ADD_REGISTER with " + line[1] + " | " + line[2] + " | " + line[3]);
                    ADD_REGISTER(line[1], line[2], line[3]);
                }
                break;
            case "subs":
                if (line[2].contains("#")){
                    System.out.println("Call SUB_8_IMMEDIATE with " + line[1] + " | " + line[2]);
                    SUB_8_IMMEDIATE(line[1], line[2]);
                }
                else if (line[3].contains("#")){
                    if ((intToBinary(line[3],0)).length() <= 3) {
                        System.out.println("Call SUB_3_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                        SUB_3_IMMEDIATE(line[1], line[2], line[3]);
                    } else if (Objects.equals(line[1], line[2])){
                        System.out.println("Call SUB_8_IMMEDIATE with " + line[1] + " | " + line[2] + " | " + line[3]);
                        SUB_8_IMMEDIATE(line[1], line[3]);
                    }
                } else {
                    System.out.println("Call SUB_REGISTER with " + line[1] + " | " + line[2] + " | " + line[3]);
                    SUB_REGISTER(line[1], line[2], line[3]);
                }
                break;
            case "movs":
                System.out.println("Call MOV_IMMEDIATE with " + line[1] + " | " + line[2]);
                MOV_IMMEDIATE(line[1], line[2]);
                break;
            case "cmp":
                if (line[2].contains("#")) {
                    System.out.println("Call CMP_IMMEDIATE with " + line[1] + " | " + line[2]);
                    CMP_IMMEDIATE(line[1], line[2]);
                } else {
                    System.out.println("Call LSR_IMMEDIATE with " + line[1] + " | " + line[2]);
                    CMP_REGISTER(line[1], line[2]);
                }
                break;
            case "ands":
                System.out.println("Call AND_REGISTER with " + line[1] + " | " + line[2]);
                AND_REGISTER(line[1], line[2]);
                break;
            case "eors":
                System.out.println("Call EOR_REGISTER with " + line[1] + " | " + line[2]);
                EOR_REGISTER(line[1], line[2]);
                break;
            case "adcs":
                System.out.println("Call ADC_REGISTER with " + line[1] + " | " + line[2]);
                ADC_REGISTER(line[1], line[2]);
                break;
            case "sbcs":
                System.out.println("Call SBC_REGISTER with " + line[1] + " | " + line[2]);
                SBC_REGISTER(line[1], line[2]);
                break;
            case "rors":
                System.out.println("Call ROR_REGISTER with " + line[1] + " | " + line[2]);
                ROR_REGISTER(line[1], line[2]);
                break;
            case "tst":
                System.out.println("Call TST_REGISTER with " + line[1] + " | " + line[2]);
                TST_REGISTER(line[1], line[2]);
                break;
            case "rsbs":
                System.out.println("Call RSB_IMMEDIATE with " + line[1] + " | " + line[2]);
                RSB_IMMEDIATE(line[1], line[2]);
                break;
            case "cmn":
                System.out.println("Call CMN_REGISTER with " + line[1] + " | " + line[2]);
                CMN_REGISTER(line[1], line[2]);
                break;
            case "orrs":
                System.out.println("Call ORR_REGISTER with " + line[1] + " | " + line[2]);
                ORR_REGISTER(line[1], line[2]);
                break;
            case "muls":
                if (line[1].equals(line[3])){
                    System.out.println("Call MUL with " + line[1] + " | " + line[2] + " | " + line[3]);
                    MUL(line[1], line[2]);
                }
                break;
            case "bics":
                System.out.println("Call BIC_REGISTER with " + line[1] + " | " + line[2]);
                BIC_REGISTER(line[1], line[2]);
                break;
            case "mvns":
                System.out.println("Call MVN_REGISTER with " + line[1] + " | " + line[2]);
                MVN_REGISTER(line[1], line[2]);
                break;
            case "str":
                if (line[2].equals("sp")) {
                    System.out.println("Call STR_IMMEDIATE with " + line[1] + " | " + line[3]);
                    STR_IMMEDIATE(line[1], line[3]);
                }
                break;
            case "ldr":
                if (line[2].equals("sp")) {
                    System.out.println("Call LDR_IMMEDIATE with " + line[1] + " | " + line[3]);
                    LDR_IMMEDIATE(line[1], line[3]);
                }
                break;
            case "add":
                if (line[1].equals("sp") && line[2].contains("#")){
                    System.out.println("Call ADD_SP_IMMEDIATE with " + line[2]);
                    ADD_SP_IMMEDIATE(line[2]);
                } else if (line[1].equals("sp") && line[2].equals("sp") && line[3].contains("#")){
                    System.out.println("Call ADD_SP_IMMEDIATE with " + line[3]);
                    ADD_SP_IMMEDIATE(line[3]);
                }
                break;
            case "sub":
                if (line[1].equals("sp") && line[2].contains("#")){
                    System.out.println("Call SUB_SP_IMMEDIATE with " + line[2]);
                    SUB_SP_IMMEDIATE(line[2]);
                } else if (line[1].equals("sp") && line[2].equals("sp") && line[3].contains("#")){
                    System.out.println("Call SUB_SP_IMMEDIATE with " + line[3]);
                    SUB_SP_IMMEDIATE(line[3]);
                }
                break;
            case "b":
                // System.out.println("Call CONDITIONAL_BRANCH with " + line[1]);
                // CONDITIONAL_BRANCH(line[1]);
                break;
            default:
                if (line[0].matches("\\d+")) {
                    // System.out.println("Call UNCONDITIONAL_BRANCH with " + line[0]);
                    // UNCONDITIONAL_BRANCH(line[0]);
                    break;
                }
                // System.out.println("/!\\ NON TRAITE DANS LE SWITCH " + line[0]);
                break;
        }
    }

    private static void CONDITIONAL_BRANCH(String s) {
    }

    private static void UNCONDITIONAL_BRANCH(String s) {

    }

    private static String binaryToHex(String binary) {

        int decimal = Integer.parseInt(binary, 2);
        StringBuilder hexStr = new StringBuilder(Integer.toString(decimal, 16));

        while (hexStr.length() != 4) {
            hexStr.insert(0, "0");
        }

        return hexStr.toString();
    }

    private static String intToBinary(String strInt, int bits) {

        String strWithoutFirstChar = strInt.substring(1);
        int Int = Integer.parseInt(strWithoutFirstChar);

        StringBuilder binStr = new StringBuilder(Integer.toBinaryString(Int));

        if (bits != 0){
            while (binStr.length() != bits) {
                binStr.insert(0, "0");
            }
        }

        return binStr.toString();
    }

    private static String intToBinaryDividedBy4(String strInt, int bits) {

        String strWithoutFirstChar = strInt.substring(1);
        int Int = Integer.parseInt(strWithoutFirstChar);

        int intDividedBy4 = Int / 4;

        return intToBinary("#" + intDividedBy4, bits);
    }

    // LSL (immediate) : Logical Shift Left
    private static void LSL_IMMEDIATE(String rd, String rm, String imm5) {
        String binary = "00000";
        binary += intToBinary(imm5, 5);
        binary += intToBinary(rm, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // LSR (immediate) : Logical Shift Right
    private static void LSR_IMMEDIATE(String rd, String rm, String imm5) {
        String binary = "00001";
        binary += intToBinary(imm5, 5);
        binary += intToBinary(rm, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // ASR (immediate) : Arithmetic Shift Right
    private static void ASR_IMMEDIATE(String rd, String rm, String imm5) {
        String binary = "00010";
        binary += intToBinary(imm5, 5);
        binary += intToBinary(rm, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // ADD (register) : Add Register
    private static void ADD_REGISTER(String rd, String rn, String rm) {
        String binary = "0001100";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rn, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // SUB (register) : Subtract Register
    private static void SUB_REGISTER(String rd, String rn, String rm) {
        String binary = "0001101";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rn, 3);
        binary += intToBinary(rd, 3);

        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // ADD (immediate) : Add 3-bit Immediate
    private static void ADD_3_IMMEDIATE(String rd, String rn, String imm3) {
        String binary = "0001110";
        binary += intToBinary(imm3, 3);
        binary += intToBinary(rn, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // SUB (immediate) : Subtract 3-bit immediate
    private static void SUB_3_IMMEDIATE(String rd, String rn, String imm3) {
        String binary = "0001111";
        binary += intToBinary(imm3, 3);
        binary += intToBinary(rn, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // MOV (immediate) : Move
    private static void MOV_IMMEDIATE(String rd, String imm8) {
        String binary = "00100";
        binary += intToBinary(rd, 3);
        binary += intToBinary(imm8, 8);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // CMP (immediate) : Compare
    private static void CMP_IMMEDIATE(String rd, String imm8) {
        String binary = "00101";
        binary += intToBinary(rd, 3);
        binary += intToBinary(imm8, 8);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // ADD (immediate) : Add 8-bit Immediate
    private static void ADD_8_IMMEDIATE(String rdn, String imm8) {
        String binary = "00110";
        binary += intToBinary(rdn,3);
        binary += intToBinary(imm8, 8);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // SUB (immediate) : Subtract 8-bit Immediate
    private static void SUB_8_IMMEDIATE(String rdn, String imm8) {
        String binary = "00111";
        binary += intToBinary(rdn,3);
        binary += intToBinary(imm8, 8);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // AND (register) : Bitwise AND
    private static void AND_REGISTER(String rdn, String rm) {
        String binary = "0100000000";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // EOR (register) : Exclusive OR
    private static void EOR_REGISTER(String rdn, String rm) {
        String binary = "0100000001";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // LSL (register) : Logical Shift Left
    private static void LSL_REGISTER(String rdn, String rm) {
        String binary = "0100000010";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // LSR (register) : Logical Shift Right
    private static void LSR_REGISTER(String rdn, String rm) {
        String binary = "0100000011";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    // ASR (register) : Arithmetic Shift Right
    private static void ASR_REGISTER(String rdn, String rm) {
        String binary = "0100000100";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //ADC (register) : Add with Carry
    private static void ADC_REGISTER(String rdn, String rm) {
        String binary = "0100000101";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //SBC (register) : Substract with Carry
    private static void SBC_REGISTER(String rdn, String rm) {
        String binary = "0100000110";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //ROR (register) : Rotate Right
    private static void ROR_REGISTER(String rdn, String rm) {
        String binary = "0100000111";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //TST (register) : Set flags on bitwise AND
    private static void TST_REGISTER(String rn, String rm) {
        String binary = "0100001000";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //RSB (immediate) : Reverse Subtract from 0
    private static void RSB_IMMEDIATE(String rd, String rn) {
        String binary = "0100001001";
        binary += intToBinary(rn, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //CMP (register) : Compare Registers
    private static void CMP_REGISTER(String rn, String rm) {
        String binary = "0100001010";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //CMN (register) : Compare Negative
    private static void CMN_REGISTER(String rn, String rm) {
        String binary = "0100001011";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //ORR (register) : Logical OR
    private static void ORR_REGISTER(String rdn, String rm) {
        String binary = "0100001100";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //MUL : Multiply Two Registers
    private static void MUL(String rdm, String rn) {
        String binary = "0100001101";
        binary += intToBinary(rn, 3);
        binary += intToBinary(rdm, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //BIC (register) : Bit Clear
    private static void BIC_REGISTER(String rdn, String rm) {
        String binary = "0100001110";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rdn, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //MVN (register) : Bitwise NOT
    private static void MVN_REGISTER(String rd, String rm) {
        String binary = "0100001111";
        binary += intToBinary(rm, 3);
        binary += intToBinary(rd, 3);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    private static void STR_IMMEDIATE(String rt, String offset) {
        String binary = "10010";
        binary += intToBinary(rt, 3);
        binary += intToBinaryDividedBy4(offset, 8);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    private static void LDR_IMMEDIATE(String rt, String offset) {
        String binary = "10011";
        binary += intToBinary(rt, 3);
        binary += intToBinaryDividedBy4(offset, 8);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //ADD (SP minus immediate) : Add Immediate from SP
    private static void ADD_SP_IMMEDIATE(String offset) {
        String binary = "101100000";
        binary += intToBinaryDividedBy4(offset, 7);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }

    //SUB (SP minus immediate) : Subtract Immediate from SP
    private static void SUB_SP_IMMEDIATE(String offset) {
        String binary = "101100001";
        binary += intToBinaryDividedBy4(offset, 7);
        hexBuffer.append(binaryToHex(binary)).append(" "); // save result in buffer
    }
}
