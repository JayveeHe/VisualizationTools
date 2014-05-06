package Main;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestGsonMain {

	public static void main(String[] args) throws IOException {
		FileInputStream is = new FileInputStream("src/Json.json");
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[512];
		int iLen = -1;
		while (-1 != (iLen = is.read(buffer)))
			os.write(buffer, 0, iLen);

		String string = new String(os.toByteArray());

		System.out.println(string);
		List<Weibo> weibos = new Gson().fromJson(string, new TypeToken<List<Weibo>>() {
		}.getType());
		System.out.println(weibos);
	}

	private static class Weibo {
		private String Text;
		private boolean Retweeted;
		private long ID;

		public String getText() {
			return Text;
		}

		public void setText(String text) {
			Text = text;
		}

		public boolean isRetweeted() {
			return Retweeted;
		}

		public void setRetweeted(boolean retweeted) {
			Retweeted = retweeted;
		}

		public long getID() {
			return ID;
		}

		public void setID(long iD) {
			ID = iD;
		}

		@Override
		public String toString() {
			return "Weibo [Text=" + Text + ", Retweeted=" + Retweeted + ", ID="
					+ ID + "]";
		}

	}
}
