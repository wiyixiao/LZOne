package com.wiyixiao.lzone;

import com.wiyixiao.lzone.utils.CRC;
import com.wiyixiao.lzone.utils.DataTransform;
import com.wiyixiao.lzone.utils.IEEE754;
import com.wiyixiao.lzone.utils.ModBusData;

import org.junit.Test;

import java.util.Objects;

/**
 * Author:Think
 * Time:2021/2/3 11:21
 * Description:This is DataTransforTest
 */
public class DataTransformTest {
    @Test
    public void bytesToFloat(){
        byte[] bytes = {(byte) 0x7B, (byte) 0x20, (byte) 0xB1, (byte) 0x2C};
        //acc6858b
        byte[] bytes1 = {(byte) 0xAC, (byte) 0xC6, (byte) 0x85, (byte) 0x8B};
        float result = IEEE754.hex2floatIeeeApi(bytes1, false);
        System.out.println(result);
    }

    @Test
    public void floatToBytes(){
        float f = 5.0342486E-12f;
        byte[] result = IEEE754.float2hexIeeeApi(f);
        System.out.println(DataTransform.bytesToHex(result));
    }

    @Test
    public void crcTest(){
        byte[] data = {
                (byte)0x07, (byte)0x06, (byte)0x03, (byte)0xE8,
                (byte)0x00, (byte)0x06
        };

        System.out.println(DataTransform.bytesToHex(CRC.getCrcModeBusData(data, true)));
    }

    @Test
    public void crcCheckTest(){
        byte[] data = {
                (byte)0x07, (byte)0x03, (byte)0x04, (byte)0x7B,
                (byte)0x20, (byte)0xB1, (byte)0x2C, (byte)0x90, (byte)0xF0
        };

        byte[] data1 = {
                (byte)0x16, (byte)0x03, (byte)0x04, (byte)0x35,
                (byte)0x80, (byte)0x92, (byte)0x2E, (byte)0x6A, (byte)0x7E
        };

        System.out.println(ModBusData.checkRevData(data1));
    }

    @Test
    public void logTest(){
        double data = 0.00000506;
        System.out.println((float)data);
        System.out.println(Math.log10(data));
        float data1= 5.06e-6f;
        System.out.println(Math.log10(data1));
        System.out.println((float) Math.log10(data));
    }

    @Test
    public void hex2StrTest(){
        String hexStr = "0F";
        byte[] hex = DataTransform.hexTobytes(DataTransform.checkHexLength(hexStr));
        System.out.println(DataTransform.bytesToHex(hex));

        byte[] res = new byte[hex.length + 2];
        System.arraycopy(hex, 0, res, 0, hex.length);
        System.arraycopy(new byte[]{0x0D, 0x0A}, 0, res, hex.length, 2);

        System.out.println(DataTransform.bytesToHex(res));
    }
}
