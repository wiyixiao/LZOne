package com.wiyixiao.lzone.utils;

import com.wiyixiao.lzone.BuildConfig;

import java.util.Arrays;

/**
 * Author:Think
 * Time:2021/2/3 10:25
 * Description:This is ModBusData
 */
public class ModBusData {

    /**
     * TODO CRC校验数据反转说明
     * 发送数据时 CRC校验数据高低位反转
     * FF 03 00 01 00 01 C014
     * 接收数据时 CRC校验数据高低不反转
     * 16 03 04 C2 CF 7C 32 A031
     */

    public static byte[] NONE_CMD = {
            (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,
            (byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF
    };

    /**
     * @Desc: 接收数据字节长度
     */
    public static int dataRevLen = 9;

    /**
     * @Desc: 发送字节长度
     */
    private static int dataSendLen = 8;

    /**
     * @Desc: 接收数据
     * example: 07 03 04 7B 20 B1 2C 90 F0
     */
    public interface dataRevIndex{
        int id_index = 0;
        int func_index = 1;
        int len_index = 2;
        int data_index = 3;
        // crc校验数据起始位置，长度2
        int crc2len_index = 7;
    }

    /**
     * @Desc: ID地址码
     */
    public interface idData{
        //万能地址，不确定设备ID时使用
        byte ALL_ID = (byte)0xFF;
    }

    /**
     * @Desc: 功能码
     */
    public interface FuncData{
        byte FUNC_A = 0x03;
        byte FUNC_B = 0x06;
        byte FUNC_C = 0x1F;
        byte FUNC_D = 0x20;
        byte FUNC_E = 0x21;
    }

    /**
     * @Desc: 有效数据
     */
    public interface ContentData{
        byte[] DATA_A = {0x00, 0x01, 0x00, 0x01};
        byte[] DATA_B = {0x03, (byte) 0xE8, 0x00, 0x06};
        byte[] DATA_C = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
    }

    /**
     * @Desc:           获取ModeBus数据
     * @param id        ID地址码                1byte
     * @param func      功能码                  1byte
     * @param content   数据内容                4byte
     * @return          带CRC校验的命令字节数组  8byte
     */
    public static byte[] getCmd(byte id, byte func, byte[] content){
        byte[] temp = new byte[6];
        temp[0] = id;
        temp[1] = func;

        System.arraycopy(content, 0, temp, 2, content.length);

        //根据前面数据获取最后两位CRC校验码，并返回拼接后的完整数据
        //数据发送时，CRC高低位反转
        return CRC.getCrcModeBusData(temp, true);
    }

    /**
     * @Desc: 接收数据校验
     * @Author: Aries.hu
     * @Date: 2021/4/12 14:51
     */
    public static boolean checkRevData(byte[] data){

        //判断长度
        if(data.length != dataRevLen){
            return false;
        }

        //CRC校验
        byte[] temp = new byte[dataRevIndex.crc2len_index];
        System.arraycopy(data, 0, temp, 0, dataRevIndex.crc2len_index);

        if(BuildConfig.DEBUG){
            System.out.println(DataTransform.bytesToHex(temp));
        }

        return Arrays.equals(data, CRC.getCrcModeBusData(temp, false));
    }

    public static float getRevData(byte[] data){
        //四字节数据内容
        byte[] temp = new byte[4];
        System.arraycopy(data, dataRevIndex.data_index, temp, 0, 4);
        if(BuildConfig.DEBUG){
            System.out.println(DataTransform.bytesToHex(temp));
        }
        return IEEE754.hex2floatIeeeApi(temp, false);
    }

}
