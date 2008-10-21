package com.konfirmagi;

import com.konfirmagi.parser.*;
import java.io.*;
import java.util.*;

public class TestParser {
    public static void main(String args[]) {
        Parser parser = new Parser(new StringReader("200 result=0 (timeoit)"));
        try {
            Hashtable table = parser.parseOneLine();
            System.out.println(table);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
