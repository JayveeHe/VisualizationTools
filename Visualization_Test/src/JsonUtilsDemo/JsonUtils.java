package JsonUtilsDemo;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.content.res.Resources;

public class JsonUtils {
	/**
	 * 把assets文件夹中的文件以字符串的形式读入内存
	 * 
	 * @param res
	 *            资源
	 * @param strFileName
	 *            assets文件夹中的文件名
	 * @return 文件内容转化的字符串
	 */
	public static String readFileFromAssets(Resources res, String strFileName) {
		String str = null;
		InputStream inpt_strm = null;
		int iLenght = 0;
		byte[] byteBuffer = null;
		// 读取文件
		try {
			inpt_strm = res.getAssets().open(strFileName);
		} catch (IOException e) {
			System.out.println("readFileFromAssets()--读取文件出错！");
			e.printStackTrace();
		}

		// 获取文件长度
		try {
			iLenght = inpt_strm.available();
		} catch (IOException e) {
			System.out.println("readFileFromAssets()--获取文件长度出错！");
			e.printStackTrace();
		}

		// 根据文件长度开辟相应的内存
		byteBuffer = new byte[iLenght];

		// 将文件内容读入内存
		try {
			inpt_strm.read(byteBuffer);
		} catch (IOException e) {
			System.out.println("readFileFromAssets()--将文件内容读取至内存出错！");
			e.printStackTrace();
		}

		try {
			inpt_strm.close();
		} catch (IOException e) {
			System.out.println("readFileFromAssets()--关闭输入流出错 ！");
			e.printStackTrace();
		}

		// 将内存中的值编码为UTF-8字符串
		str = EncodingUtils.getString(byteBuffer, "UTF-8");

		return str;
	}
}
