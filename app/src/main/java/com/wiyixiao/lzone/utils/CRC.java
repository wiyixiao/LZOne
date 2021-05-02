package com.wiyixiao.lzone.utils;

import java.util.Arrays;

/**
 * Author:Think
 * Time:2021/2/3 10:39
 * Description:This is CRC
 */
public class CRC {

    /**
     * CRC-16 (Modbus)
     * CRC16_MODBUS：多项式x16+x15+x2+1（0x8005），初始值0xFFFF，低位在前，高位在后，结果与0x0000异或
     * 0xA001是0x8005按位颠倒后的结果
     * @param buffer
     * @return
     */
    public static int CRC16_MODBUS_RTU(byte[] buffer) {
        int wcrcin = 0xffff;
        int POLYNOMIAL = 0xa001;
        for (byte b : buffer) {
            wcrcin ^= ((int) b & 0x00ff);
            for (int j = 0; j < 8; j++) {
                if ((wcrcin & 0x0001) != 0) {
                    wcrcin >>= 1;
                    wcrcin ^= POLYNOMIAL;
                } else {
                    wcrcin >>= 1;
                }
            }
        }
        return wcrcin ^= 0x0000;
    }

    public static byte[] CRC16_8_swap (int crc16) { // 交换CRC的前八位后八位
//        byte h8 = (byte) ((crc16 >> 8) & 0xFF);
//        byte l8 = (byte) (crc16 & 0xFF);
//        return (h8 & 0xFF) | (l8 & 0xFF) << 8;
        return new byte[] {(byte) ((crc16) & 0xFF), (byte) (crc16 >> 8 & 0xFF)};
    }

    public static byte[] intToByte (int x) {
        return new byte[] {(byte) ((x >> 8) & 0xFF), (byte) (x & 0xFF)};
    }

    public static byte[] arrayMerge (byte[] a, byte[] b) {
        for (byte bs : b) {
            a = Arrays.copyOf(a, a.length + 1);
            a[a.length - 1] = bs;
        }
        return a;
    }

    /**
     * @Desc: ModeBus CRC 数据校验
     * @reverse 是否反转CRC数据高低位
     */
    public static byte[] getCrcModeBusData(byte[] data, boolean reverse){
        int crc_data = CRC16_MODBUS_RTU(data);
        byte temp[] = null;

        if(reverse){
            temp = CRC16_8_swap(crc_data);
        }else {
            temp = intToByte(crc_data);
        }

        //拼接校验数据
        byte[] result = new byte[data.length + temp.length];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(temp, 0, result, data.length, temp.length);

        return result;
    }

}
