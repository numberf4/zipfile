package vn.tapbi.zazip.utils;

import java.util.HashMap;

public class ComparatorVietnamese {
    public ComparatorVietnamese() {
        createCode();
        createCodeDesc();
    }


    private HashMap<Character, String> codeVN = new HashMap<>();
    private HashMap<Character, String> codeVNDesc = new HashMap<>();

    private void createCode() {
        codeVN.put('0', "000");
        codeVN.put('1', "001");
        codeVN.put('2', "002");
        codeVN.put('3', "003");
        codeVN.put('4', "004");
        codeVN.put('5', "005");
        codeVN.put('6', "006");
        codeVN.put('7', "007");
        codeVN.put('8', "008");
        codeVN.put('9', "009");
        codeVN.put('a', "010");
        codeVN.put('à', "011");
        codeVN.put('á', "012");
        codeVN.put('ả', "013");
        codeVN.put('ã', "014");
        codeVN.put('ạ', "015");
        codeVN.put('ă', "016");
        codeVN.put('ằ', "017");
        codeVN.put('ắ', "018");
        codeVN.put('ẳ', "019");
        codeVN.put('ẵ', "020");
        codeVN.put('ặ', "021");
        codeVN.put('â', "022");
        codeVN.put('ấ', "023");
        codeVN.put('ầ', "024");
        codeVN.put('ẩ', "025");
        codeVN.put('ẫ', "026");
        codeVN.put('ậ', "027");
        codeVN.put('b', "028");
        codeVN.put('c', "029");
        codeVN.put('d', "030");
        codeVN.put('đ', "031");
        codeVN.put('e', "032");
        codeVN.put('è', "033");
        codeVN.put('é', "034");
        codeVN.put('ẻ', "035");
        codeVN.put('ẽ', "036");
        codeVN.put('ẹ', "037");
        codeVN.put('ê', "038");
        codeVN.put('ề', "039");
        codeVN.put('ế', "040");
        codeVN.put('ể', "041");
        codeVN.put('ễ', "042");
        codeVN.put('ệ', "043");
        codeVN.put('f', "044");
        codeVN.put('g', "045");
        codeVN.put('h', "046");
        codeVN.put('i', "047");
        codeVN.put('í', "048");
        codeVN.put('ì', "049");
        codeVN.put('ỉ', "050");
        codeVN.put('ĩ', "051");
        codeVN.put('ị', "052");
        codeVN.put('j', "053");
        codeVN.put('k', "054");
        codeVN.put('l', "055");
        codeVN.put('m', "056");
        codeVN.put('n', "057");
        codeVN.put('o', "058");
        codeVN.put('ò', "059");
        codeVN.put('ó', "060");
        codeVN.put('ỏ', "061");
        codeVN.put('õ', "062");
        codeVN.put('ọ', "063");
        codeVN.put('ô', "064");
        codeVN.put('ồ', "065");
        codeVN.put('ố', "066");
        codeVN.put('ổ', "067");
        codeVN.put('ỗ', "068");
        codeVN.put('ộ', "069");
        codeVN.put('ơ', "070");
        codeVN.put('ờ', "071");
        codeVN.put('ớ', "072");
        codeVN.put('ở', "073");
        codeVN.put('ỡ', "074");
        codeVN.put('ợ', "075");
        codeVN.put('p', "076");
        codeVN.put('q', "077");
        codeVN.put('r', "078");
        codeVN.put('s', "079");
        codeVN.put('t', "080");
        codeVN.put('u', "081");
        codeVN.put('ù', "082");
        codeVN.put('ú', "083");
        codeVN.put('ủ', "084");
        codeVN.put('ũ', "085");
        codeVN.put('ụ', "086");
        codeVN.put('ư', "087");
        codeVN.put('ừ', "088");
        codeVN.put('ứ', "089");
        codeVN.put('ử', "090");
        codeVN.put('ữ', "091");
        codeVN.put('ự', "092");
        codeVN.put('v', "093");
        codeVN.put('x', "094");
        codeVN.put('y', "095");
        codeVN.put('z', "096");
        codeVN.put(' ', "097");
        codeVN.put('-', "098");
        codeVN.put('/', "099");
    }
    private void createCodeDesc() {
        codeVNDesc.put('/', "000");
        codeVNDesc.put('-', "001");
        codeVNDesc.put(' ', "002");
        codeVNDesc.put('z', "003");
        codeVNDesc.put('y', "004");
        codeVNDesc.put('x', "005");
        codeVNDesc.put('v', "006");
        codeVNDesc.put('ự', "007");
        codeVNDesc.put('ữ', "008");
        codeVNDesc.put('ử', "009");
        codeVNDesc.put('ứ', "010");
        codeVNDesc.put('ừ', "011");
        codeVNDesc.put('ư', "012");
        codeVNDesc.put('ụ', "013");
        codeVNDesc.put('ũ', "014");
        codeVNDesc.put('ủ', "015");
        codeVNDesc.put('ú', "016");
        codeVNDesc.put('ù', "017");
        codeVNDesc.put('u', "018");
        codeVNDesc.put('t', "019");
        codeVNDesc.put('s', "020");
        codeVNDesc.put('r', "021");
        codeVNDesc.put('q', "022");
        codeVNDesc.put('p', "023");
        codeVNDesc.put('ợ', "024");
        codeVNDesc.put('ỡ', "025");
        codeVNDesc.put('ở', "026");
        codeVNDesc.put('ớ', "027");
        codeVNDesc.put('ờ', "028");
        codeVNDesc.put('ơ', "029");
        codeVNDesc.put('ộ', "030");
        codeVNDesc.put('ỗ', "031");
        codeVNDesc.put('ổ', "032");
        codeVNDesc.put('ố', "033");
        codeVNDesc.put('ồ', "034");
        codeVNDesc.put('ô', "035");
        codeVNDesc.put('ọ', "036");
        codeVNDesc.put('õ', "037");
        codeVNDesc.put('ỏ', "038");
        codeVNDesc.put('ó', "039");
        codeVNDesc.put('ò', "040");
        codeVNDesc.put('o', "041");
        codeVNDesc.put('n', "042");
        codeVNDesc.put('m', "043");
        codeVNDesc.put('l', "044");
        codeVNDesc.put('k', "045");
        codeVNDesc.put('j', "046");
        codeVNDesc.put('ị', "047");
        codeVNDesc.put('ĩ', "048");
        codeVNDesc.put('ỉ', "049");
        codeVNDesc.put('í', "050");
        codeVNDesc.put('ì', "051");
        codeVNDesc.put('i', "052");
        codeVNDesc.put('h', "053");
        codeVNDesc.put('g', "054");
        codeVNDesc.put('f', "055");
        codeVNDesc.put('ệ', "056");
        codeVNDesc.put('ễ', "057");
        codeVNDesc.put('ể', "058");
        codeVNDesc.put('ế', "059");
        codeVNDesc.put('ề', "060");
        codeVNDesc.put('ê', "061");
        codeVNDesc.put('ẹ', "062");
        codeVNDesc.put('ẽ', "063");
        codeVNDesc.put('ẻ', "064");
        codeVNDesc.put('é', "065");
        codeVNDesc.put('è', "066");
        codeVNDesc.put('e', "067");
        codeVNDesc.put('đ', "068");
        codeVNDesc.put('d', "069");
        codeVNDesc.put('c', "070");
        codeVNDesc.put('b', "071");
        codeVNDesc.put('ậ', "072");
        codeVNDesc.put('ẫ', "073");
        codeVNDesc.put('ẩ', "074");
        codeVNDesc.put('ấ', "075");
        codeVNDesc.put('ầ', "076");
        codeVNDesc.put('â', "077");
        codeVNDesc.put('ặ', "078");
        codeVNDesc.put('ẵ', "079");
        codeVNDesc.put('ẳ', "080");
        codeVNDesc.put('ắ', "081");
        codeVNDesc.put('ằ', "082");
        codeVNDesc.put('ă', "083");
        codeVNDesc.put('ạ', "084");
        codeVNDesc.put('ã', "085");
        codeVNDesc.put('ả', "086");
        codeVNDesc.put('á', "087");
        codeVNDesc.put('à', "088");
        codeVNDesc.put('a', "089");
        codeVNDesc.put('9', "090");
        codeVNDesc.put('8', "091");
        codeVNDesc.put('7', "092");
        codeVNDesc.put('6', "093");
        codeVNDesc.put('5', "094");
        codeVNDesc.put('4', "095");
        codeVNDesc.put('3', "096");
        codeVNDesc.put('2', "097");
        codeVNDesc.put('1', "098");
        codeVNDesc.put('0', "099");
    }

    public String generator(String input) {
        StringBuilder result = new StringBuilder();
        char[] b = input.toLowerCase().toCharArray();
        for (int i = 0; i < b.length; i++) {
            result.append(codeVN.get(b[i]));
        }
        return result.toString();
    }
    public String generatorDesc(String input) {
        StringBuilder result = new StringBuilder();
        char[] b = input.toLowerCase().toCharArray();
        for (int i = 0; i < b.length; i++) {
            result.append(codeVNDesc.get(b[i]));
        }
        return result.toString();
    }

}
