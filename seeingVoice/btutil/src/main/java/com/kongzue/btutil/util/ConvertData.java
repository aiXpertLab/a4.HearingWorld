package com.kongzue.btutil.util;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

public class ConvertData
{

	/**
	 * int 转换为hexString
	 */
	public static String intToHexStringLe(int value) {
		String hexStr = "0123456789ABCDEF";
		String result = "";
		String hex = "";

		byte[] bytes = new byte[2];
		short shortValue = (short)value;
		for (int i = 0; i < bytes.length; ++i) {
			bytes[(bytes.length - i - 1)] = (byte) (shortValue >> i * 8 & 0xFF);
		}

		for (byte b : bytes) {
			hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
			hex += String.valueOf(hexStr.charAt(b & 0x0F));
			result += hex;
		}
		return result;
	}
	/**
	 * shot 转换为byte[]
	 */
	public static byte[] shortToByte(int value){

		byte[] bytes = new byte[2];
		short shortValue = (short)value;
		for (int i = 0; i < bytes.length; ++i) {
			bytes[(bytes.length - i - 1)] = (byte) (shortValue >> i * 8 & 0xFF);
		}


		return bytes;
	}
	/**最大发送20个字节，但是首个字节由标志位占用*/
	private static final int MAX_SIZE = 9;
	/**
	 * 字符串转为二维字节数组
	 * */
	public static byte[][] encode(String strData) {
		try{
			byte[] originData = hexStringToBytes(str2HexStr(strData,false));
			int size = (int)Math.ceil(originData.length / (MAX_SIZE*1.0));
			byte[][] data = new byte[size][MAX_SIZE+1];

			int start = 0;
			int end = 0;
			int index  = 0;
			while(index < size) {
				index ++ ;
//				if(index == size) {
//					data[index-1][0] = END_BYTE;
//				} else if(index == 1) {
//					data[index-1][0] = START_BYTE;
//				} else {
//					data[index-1][0] = CONTINUE_BYTE;
//				}


				end = Math.min(start + MAX_SIZE, originData.length);
				System.arraycopy(originData, start, data[index-1], 1, end - start);


				start = end;
			}

			return data;
		}catch(Exception e){
			e.printStackTrace();
		}

		return null;

	}

	/**
	 *将utf-8 string 转换为16进制byte
	 */
	public static byte[] stringToHexBytes(String sendString){
		String result = null;
		String HexString = null;
		byte[] bytes = new byte[0];
		if(sendString == null || sendString.isEmpty())
			return null;
		try{
			result = new String(sendString.getBytes("UTF-8"),"UTF-8");
		}catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		HexString = str2HexStr(result,false);
		bytes = hexStringToBytes(HexString);

		Log.d("ljptest","发送2：\r\n"+Arrays.toString(bytes));
		return bytes;
	}
	/**
	 * 字符串转换成十六进制字符串
	 * @param  str 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str, boolean bSpace)
	{

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++)
		{

			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			if(bSpace) {
				sb.append(' ');
			}

		}
		return sb.toString().trim();
	}
    /**
     * 将byte数组转换为16进制格式的字符串
     *
     * @param bytes byte数组
     * @param bSpace  是否在每两个数组中间添加空格
     *
     * @return 返回16进制格式的字符串
     */
	public static String bytesToHexString(byte[] bytes, boolean bSpace)
	{
		if(bytes == null || bytes.length <= 0)
			return null;
		
		StringBuffer stringBuffer = new StringBuffer(bytes.length);
		String sTemp;
		
		for (int i = 0; i < bytes.length; i++) 
		{
			sTemp = Integer.toHexString(0xFF & bytes[i]);
			
			if (sTemp.length() < 2)
				stringBuffer.append(0);
			
			stringBuffer.append(sTemp);
			
			if(bSpace)
				stringBuffer.append(" ");
		}
		return stringBuffer.toString();
	}
	
    /**
     * 将字符串转换为byte数组
     *
     * @param hexString 16进制格式的字符串（仅包含0-9，a-f,A-F,且长度为偶数)
     *
     * @return 返回转换后的byte数组
     */
	public static byte[] hexStringToBytes(String hexString)
	{
		if(hexString == null)
			return null;
		
		hexString = hexString.replace(" ", "");
		hexString = hexString.toUpperCase();
		
		int len = (hexString.length() / 2);
		if(len <= 0)
			return null;
		
		byte[] result = new byte[len];
		char[] achar = hexString.toCharArray();
		for (int i = 0; i < len; i++) 
		{
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		
		return result;
	}
	
    /**
     * 将一个数组拷贝到另一个数组上
     *
     * @param dst 目标数组
     * @param dstOffset 目标数组偏移
     * @param src 源数组
     * @param srcOffset 目标数组偏移
     * @param length 拷贝的长度
     *
     * @return 成功返回true，否则false
     */
	public static boolean cpyBytes(byte[] dst, int dstOffset, byte[] src, int srcOffset, int length)
	{
		if(dst == null || src == null || 
				dstOffset > dst.length || srcOffset > src.length ||
				length > (dst.length-dstOffset) || length > (src.length-srcOffset))
		{
			return false;
		}
		
		for (int i = 0; i < length; i++)
		{
			dst[i+dstOffset] = src[i+srcOffset];
		}
		
		return true;
	}
	
    /**
     * 两个数组比较
     *
     * @param data1 数组1
     * @param data2 数组2
     *
     * @return 相等返回true，否则返回false
     */
	public static boolean cmpBytes(byte[] data1, byte[] data2)
	{
		if (data1 == null && data2 == null)
		{
			return true;
		}
		if (data1 == null || data2 == null) 
		{
			return false;
		}
		if (data1 == data2)
		{
			return true;
		}
		if(data1.length != data2.length)
		{
			return false;
		}
		
		int len = data1.length;
		for (int i = 0; i < len; i++)
		{
			if(data1[i] != data2[i])
				return false;
		}
		
		return true;
	}
	
	private static int toByte(char c) 
	{
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);
	    return b;
	 }
	/**
	 *下面这个方法目前还没有用到
	 */
	private byte[] getHexBytes(String message) {
		int len = message.length() / 2;
		char[] chars = message.toCharArray();
		String[] hexStr = new String[len];
		byte[] bytes = new byte[len];
		for (int i = 0, j = 0; j < len; i += 2, j++) {
			hexStr[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
		}
		return bytes;
	}
	/**
	 * 16进制直接转换成为字符串(无需Unicode解码)
	 * @param hexStr
	 * @return
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}
	/**
	 * 发送数据
	 */
//	public static byte[] sendFormatData(int totalLength,byte messageType,byte[] text){
//		//totalLength 转换为bytes占4位，messageType占1位，头尾占2位
//		byte[] result = new byte[text.length+4+1+2];
//		byte[] length = new byte[4];
//
//		result[0] = Constances.START_END;
//		length = intToBytes(totalLength);
//		result[1] = length[0];
//		result[2] = length[1];
//		result[3] = length[2];
//		result[4] = length[3];
//
//		result[5] = messageType;
//		for(int i = 6;i<result.length;i++){
//			result[i] = text[i-6];
//		}
//		result[result.length-1] = Constances.START_END;
//
//		for(int i = 0;i<result.length;i++){
//			Log.d("ljp","result{"+i+":"+result[i]);
//		}
//		return result;
//	}
	/**
	 * 接收返回的数据
	 */
	public static String getData(byte[] data){
		String str = bytesToHexString(data,false);
		return str;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes( int value )
	{
		byte[] src = new byte[4];
		src[3] =  (byte) ((value>>24) & 0xFF);
		src[2] =  (byte) ((value>>16) & 0xFF);
		src[1] =  (byte) ((value>>8) & 0xFF);
		src[0] =  (byte) (value & 0xFF);
		return src;
	}

	/**
	 * 对电池电量进行解析
	 * @param hexStr
	 * @return
	 */
	public static short hexStrToShortLogin(String hexStr){
		if(hexStr == null || hexStr.equals(""))
			return 0;
		String[] splited = hexStr.split("\\s+");
		StringBuilder strBuild = new StringBuilder();

		if(splited.length >14 && splited[12]!=null && splited[13]!=null) {
			Log.d("ljp","电池电量："+splited[12]+splited[13]);
			strBuild.append(splited[12]).append(splited[13]);
			short iValue = Short.parseShort(strBuild.toString(), 16);

			return iValue;
		}else{
			return 0;
		}
	}
	public static short hexStrToShortHeart(String hexStr){
		if(hexStr == null || hexStr.equals(""))
			return 0;
		String[] splited = hexStr.split("\\s+");
		StringBuilder strBuild = new StringBuilder();

		if(splited.length >5 && splited[4]!=null && splited[5]!=null) {
			strBuild.append(splited[4]).append(splited[5]);
			short iValue = Short.parseShort(strBuild.toString(), 16);

			return iValue;
		}else{
			return 0;
		}
	}
	/**
	 * int到byte[]
	 * @param i
	 * @return
	 */
	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		// 由高位到低位
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}
	/**
	 * 获取时间戳16进制字符串
	 */
	public static String getCurrentTimeHexStr(){
		long testLong = new Date().getTime()/1000;
		int testInt = Integer.parseInt(String.valueOf(testLong));

		byte[] testintByte = new byte[0];
		testintByte = intToByteArray(testInt);
		Log.d("ljp","当前intg时间："+testInt+"时间戳转换："+ConvertData.bytesToHexString(testintByte,false));

		return ConvertData.bytesToHexString(testintByte,false);
	}
	public static String makeChecksum(String data) {
		if (data == null || data.equals("")) {
			return "";
		}
		int total = 0;
		int len = data.length();
		int num = 0;
		while (num < len) {
			String s = data.substring(num, num + 2);
			total += Integer.parseInt(s, 16);
			num = num + 2;
		}
		/**
		 * 用256求余最大是255，即16进制的FF
		 */
		int mod = total % 256;
		String hex = Integer.toHexString(mod);
		len = hex.length();
		// 如果不够校验位的长度，补0,这里用的是两位校验
		if (len < 2) {
			hex = "0" + hex;
		}
		return hex;
	}
	public static String specialCharReplace(String str){

		StringBuilder strBuild = new StringBuilder();
		StringBuilder resultBuild = new StringBuilder();
		for(int i = 0;i<str.length();i++){
			strBuild.append(str.charAt(i)).append(str.charAt(i+1)).append(" ");
			i++;
//            Log.d("ljp",strBuild.toString());
		}
		String[] split = strBuild.toString().split(" ");
		for(int i=0 ;i<split.length;i++){
			if(split[i].equals("7e")){
				split[i] = "7e03";
			}else if(split[i].equals("7f")){
				split[i] = "7e01";
			}else if(split[i].equals("f7")){
				split[i] = "7e02";
			}
			resultBuild.append(split[i]);
		}

		return resultBuild.toString();
	}
}
