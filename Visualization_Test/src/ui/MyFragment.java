package ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import surfaceview_Main.SurfaceViewMain;
import jayvee.visualization_weibo.R;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieGraph.OnSliceClickedListener;
import com.echo.holographlibrary.PieSlice;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import SQLiteUtils.DBhelper;
import Utils.FileUtils;
import Utils.NetworkUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment {
	int mNum;// 页号
	private String userID;
	private String wid;
	private boolean hasDate = true;

	int Vtexin;
	int Vtexout;
	double ClusterCoefficient;
	double truefollRatio;
	double BilateralRatio;
	int woman;
	int man;

	JSONArray array_recentlist;
	JSONArray array_repostline;
	JSONArray array_province;

	private int weiboNo = 0;
	private String user_datas;
	private String recentweibolist;
	private String weibocurve;

	/**
	 * 实例化一个MyFragment对象
	 * 
	 * @param num
	 *            该对象所对应的页号
	 * @return
	 */
	public static MyFragment newInstance(int num) {
		MyFragment myFragment = new MyFragment();
		Bundle args = new Bundle();// 启动该fragment时的参数
		args.putInt("viewNum", num);
		myFragment.setArguments(args);
		return myFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("oncreate");

		// 读取数据
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("weiboName",
				ViewpagerActivity.userName));
		user_datas = null;
		try {
			user_datas = new String(FileUtils.File2byte(getActivity()
					.getExternalCacheDir()
					+ File.separator
					+ "datas"
					+ File.separator + ViewpagerActivity.userName + ".data"),
					"utf-8");
			JSONObject root = (JSONObject) new JSONTokener(user_datas)
					.nextValue();
			userID = root.getString("userID");
			Vtexin = root.getInt("Vtexin");
			Vtexout = root.getInt("Vtexout");
			ClusterCoefficient = root.getDouble("ClusterCoefficient");
			truefollRatio = root.getDouble("truefollRatio");
			BilateralRatio = root.getDouble("BilateralRatio");
			woman = root.getInt("woman");
			man = root.getInt("man");
			array_recentlist = root.getJSONArray("recentWeibo");
			array_province = root.getJSONArray("province");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 此处可以给每个page对应一个独立的布局
		System.out.println("oncreateview");
		View view;
		mNum = getArguments() != null ? mNum = getArguments().getInt("viewNum")
				: 0;
		switch (mNum) {
		case 0:// 测试中，0为饼图
			view = inflater.inflate(R.layout.fragment_sex, container, false);
			SetSexPie(view);
			return view;
		case 1:// 测试中，1为曲线图
			view = inflater.inflate(R.layout.fragment_postline, container,
					false);
			try {
				SetRepostLine(view);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return view;
		case 2:// 测试中，2为柱状图
			view = inflater.inflate(R.layout.fragment_userparams, container,
					false);
			SetParamsBar(view);
			return view;
		case 3:// 地域分布图
			view = inflater.inflate(R.layout.fragment_province, container,
					false);
			SetProvincePie(view);
			return view;

		}
		return null;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		System.out.println("onviewcreated");
		mNum = getArguments() != null ? mNum = getArguments().getInt("viewNum")
				: 0;
	}

	private void SetSexPie(View view) {
		final PieGraph pg = (PieGraph) view.findViewById(R.id.graph_sex);
		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#33B5E5"));
		slice.setValue(man);
		slice.setTitle("男性");
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#FF4444"));
		slice.setValue(woman);
		slice.setTitle("女性");
		pg.addSlice(slice);
		// slice = new PieSlice();
		// slice.setColor(Color.parseColor("#AA66CC"));
		// slice.setValue(8);
		// slice.setTitle("第三个");
		// pg.addSlice(slice);

		pg.setInnerCircleRatio(150);
		pg.setPadding(1);

		pg.setLabelText(pg.getSlice(0).getTitle());
		pg.setLabelColor(pg.getSlice(0).getColor());
		pg.setLabelPercentage(pg.getSlice(0).getValue());

		pg.setOnSliceClickedListener(new OnSliceClickedListener() {

			@Override
			public void onClick(int index) {
				pg.setLabelText(pg.getSlice(index).getTitle());
				pg.setLabelColor(pg.getSlice(index).getColor());
				pg.setLabelPercentage(pg.getSlice(index).getValue());
			}
		});

		// 描述文字
		int countboy = (int) pg.getSlice(0).getValue();
		int countgirl = (int) pg.getSlice(1).getValue();
		float total = countboy + countgirl;
		float percentboy = countboy / total * 100f;
		float percentgirl = countgirl / total * 100f;
		DecimalFormat fnum = new DecimalFormat("##0.0");
		String strboy = ViewpagerActivity.userName + "的好友当中" + "男性" + "共有"
				+ countboy + "位,占" + fnum.format(percentboy) + "%\n";
		String strgirl = "女性" + "共有" + countgirl + "位,占"
				+ fnum.format(percentgirl) + "%\n";
		String strcomment = "";

		String msg = null;// 相关说明的字符串形式
		SpannableString msp = null;
		msg = strboy + strgirl + strcomment;
		// msg = "您的粉丝当中" + "男生" + "共有" + countboy + "位,占"
		// + fnum.format(percentboy) + "%\n" + "女生" + "共有" + countgirl
		// + "位,占" + fnum.format(percentgirl) + "%\n";

		// 格式设置
		msp = new SpannableString(msg);
		// 主角名
		msp.setSpan(new ForegroundColorSpan(Color.parseColor("#7C07AA")), 0,
				ViewpagerActivity.userName.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.8f), 0,
				ViewpagerActivity.userName.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 男生
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(0).getColor()),
				ViewpagerActivity.userName.length() + 5,
				ViewpagerActivity.userName.length() + 7,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f),
				ViewpagerActivity.userName.length() + 5,
				ViewpagerActivity.userName.length() + 7,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 男生个数
		msp.setSpan(
				new ForegroundColorSpan(pg.getSlice(0).getColor()),
				ViewpagerActivity.userName.length() + 9,
				ViewpagerActivity.userName.length() + 9
						+ String.valueOf(countboy).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(
				new RelativeSizeSpan(1.5f),
				ViewpagerActivity.userName.length() + 9,
				ViewpagerActivity.userName.length() + 9
						+ String.valueOf(countboy).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 男生百分比
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(0).getColor()),
				strboy.length() - 6, strboy.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(2f), strboy.length() - 6,
				strboy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		// 女生
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(1).getColor()),
				strboy.length(), strboy.length() + 2,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), strboy.length(),
				strboy.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 女生个数
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(1).getColor()),
				strboy.length() + 4,
				strboy.length() + 4 + String.valueOf(countgirl).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.5f), strboy.length() + 4,
				strboy.length() + 4 + String.valueOf(countgirl).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 女生百分比
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(1).getColor()),
				strboy.length() + strgirl.length() - 6, strboy.length()
						+ strgirl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(2f),
				strboy.length() + strgirl.length() - 6, strboy.length()
						+ strgirl.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 评论字体
		msp.setSpan(new ForegroundColorSpan(Color.parseColor("#99CC00")),
				msg.length() - strcomment.length(), msg.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), msg.length()
				- strcomment.length(), msg.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		TextView text_dscp_sex = (TextView) view
				.findViewById(R.id.text_dscp_sex);
		text_dscp_sex.setText(msp);
		text_dscp_sex.setTextSize(22);

	}

	private void SetProvincePie(View view) {
		final PieGraph pg_province = (PieGraph) view
				.findViewById(R.id.graph_province);
		String[] colors = new String[] { "#F90012", "#FF9400", "#FFE500",
				"#B7F200", "#41DB00", "#00B15C", "#0772A0", "#113FAA",
				"#4111AE", "#7807A9", "#CC0073" };
		int friends_count = 0;
		class provincedata implements Comparable<provincedata> {

			private String name;
			private int value;
			private int color;

			provincedata(String name, int value, int color) {
				this.name = name;
				this.value = value;
				this.color = color;
			}

			@Override
			public int compareTo(provincedata arg0) {
				// TODO Auto-generated method stub
				return arg0.getValue() - value;
			}

			public String getName() {
				return name;
			}

			public int getValue() {
				return value;
			}

			public int getColor() {
				return color;
			}

			public void setColor(int color) {
				this.color = color;
			}

		}

		ArrayList<provincedata> datalist = new ArrayList<provincedata>();
		for (int i = 0; i < array_province.length(); i++) {
			JSONObject province;
			try {
				province = (JSONObject) array_province.get(i);
				provincedata pd = new provincedata(province.getString("name"),
						province.getInt("value"), Color.parseColor(colors[i
								% colors.length]));
				datalist.add(pd);
				friends_count += province.getInt("value");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Collections.sort(datalist);
		for (int i = 0; i < datalist.size(); i++) {
			provincedata pd = datalist.get(i);
			pd.setColor(Color.parseColor(colors[i % colors.length]));
			PieSlice slice = new PieSlice();
			slice.setColor(pd.getColor());
			slice.setTitle(pd.getName());
			slice.setValue(pd.getValue());
			pg_province.addSlice(slice);
		}

		pg_province.setInnerCircleRatio(150);
		pg_province.setPadding(1);

		pg_province.setLabelText(pg_province.getSlice(0).getTitle());
		pg_province.setLabelColor(pg_province.getSlice(0).getColor());
		pg_province.setLabelPercentage(pg_province.getSlice(0).getValue());

		pg_province.setOnSliceClickedListener(new OnSliceClickedListener() {

			@Override
			public void onClick(int index) {
				// TODO Auto-generated method stub
				// Toast.makeText(getActivity(), pg.getSlice(index).getTitle(),
				// Toast.LENGTH_SHORT).show();
				pg_province
						.setLabelText(pg_province.getSlice(index).getTitle());
				pg_province.setLabelColor(pg_province.getSlice(index)
						.getColor());
				pg_province.setLabelPercentage(pg_province.getSlice(index)
						.getValue());
			}
		});

		// 说明部分的格式设置
		TextView text_province = (TextView) view
				.findViewById(R.id.text_dscp_province);
		String overall = "在" + ViewpagerActivity.userName + "的" + friends_count
				+ "个好友当中，";
		String first = "来自" + datalist.get(0).getName() + "的好友共有"
				+ datalist.get(0).getValue() + "个，排名第一；";
		String second = "来自" + datalist.get(1).getName() + "的好友共有"
				+ datalist.get(1).getValue() + "个，排名第二；";
		String third = "来自" + datalist.get(2).getName() + "的好友共有"
				+ datalist.get(2).getValue() + "个，排名第三。";
		String comment = overall + first + second + third;
		SpannableString msp = new SpannableString(comment);
		// 用户名
		msp.setSpan(new ForegroundColorSpan(Color.parseColor("#A101A6")), 1,
				ViewpagerActivity.userName.length() + 1,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), 1,
				ViewpagerActivity.userName.length() + 1,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 用户好友数
		msp.setSpan(
				new ForegroundColorSpan(Color.parseColor("#A101A6")),
				2 + ViewpagerActivity.userName.length(),
				2 + ViewpagerActivity.userName.length()
						+ String.valueOf(friends_count).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(
				new RelativeSizeSpan(1.2f),
				2 + ViewpagerActivity.userName.length(),
				2 + ViewpagerActivity.userName.length()
						+ String.valueOf(friends_count).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 第一
		// //省份
		msp.setSpan(new ForegroundColorSpan(datalist.get(0).getColor()),
				overall.length() + 2, overall.length() + 2
						+ datalist.get(0).getName().length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.6f), overall.length() + 2,
				overall.length() + 2 + datalist.get(0).getName().length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// //个数
		msp.setSpan(new ForegroundColorSpan(datalist.get(0).getColor()),
				overall.length() + 7 + datalist.get(0).getName().length(),
				overall.length() + 7 + datalist.get(0).getName().length()
						+ String.valueOf(datalist.get(0).getValue()).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.6f), overall.length() + 7
				+ datalist.get(0).getName().length(),
				overall.length() + 7 + datalist.get(0).getName().length()
						+ String.valueOf(datalist.get(0).getValue()).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		// 第二
		// //省份
		msp.setSpan(new ForegroundColorSpan(datalist.get(1).getColor()),
				overall.length() + first.length() + 2,
				overall.length() + first.length() + 2
						+ datalist.get(1).getName().length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.4f),
				overall.length() + first.length() + 2,
				overall.length() + first.length() + 2
						+ datalist.get(1).getName().length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// //个数
		msp.setSpan(
				new ForegroundColorSpan(datalist.get(1).getColor()),
				overall.length() + first.length() + 7
						+ datalist.get(1).getName().length(),
				overall.length() + first.length() + 7
						+ datalist.get(1).getName().length()
						+ String.valueOf(datalist.get(1).getValue()).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(
				new RelativeSizeSpan(1.2f),
				overall.length() + first.length() + 7
						+ datalist.get(1).getName().length(),
				overall.length() + first.length() + 7
						+ datalist.get(1).getName().length()
						+ String.valueOf(datalist.get(1).getValue()).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		// 第三
		// //省份
		msp.setSpan(new ForegroundColorSpan(datalist.get(2).getColor()),
				overall.length() + first.length() + second.length() + 2,
				overall.length() + first.length() + second.length() + 2
						+ datalist.get(2).getName().length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f),
				overall.length() + first.length() + second.length() + 2,
				overall.length() + first.length() + second.length() + 2
						+ datalist.get(2).getName().length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// //个数
		msp.setSpan(
				new ForegroundColorSpan(datalist.get(2).getColor()),
				overall.length() + first.length() + second.length() + 7
						+ datalist.get(2).getName().length(),
				overall.length() + first.length() + second.length() + 7
						+ datalist.get(2).getName().length()
						+ String.valueOf(datalist.get(2).getValue()).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(
				new RelativeSizeSpan(1.2f),
				overall.length() + first.length() + second.length() + 7
						+ datalist.get(2).getName().length(),
				overall.length() + first.length() + second.length() + 7
						+ datalist.get(2).getName().length()
						+ String.valueOf(datalist.get(2).getValue()).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		text_province.setText(msp);
		text_province.setTextSize(22);

	}

	private void SetParamsBar(View view) {
		final BarGraph bg_ClusterCoefficient = (BarGraph) view
				.findViewById(R.id.graph_ClusterCoefficient);
		final BarGraph bg_truefollRatio = (BarGraph) view
				.findViewById(R.id.graph_TruefollRatio);
		final BarGraph bg_BilateralRatio = (BarGraph) view
				.findViewById(R.id.graph_BilateralRatio);
		ArrayList<Bar> bars_ClusterCoefficient = new ArrayList<Bar>();
		ArrayList<Bar> bars_truefollRatio = new ArrayList<Bar>();
		ArrayList<Bar> bars_BilateralRatio = new ArrayList<Bar>();
		// 好友紧密度
		Bar tightness = new Bar();
		tightness.setName("好友紧密度");
		tightness.setValue((float) ClusterCoefficient);
		tightness.setColor(Color.parseColor("#33B5E5"));
		bars_ClusterCoefficient.add(tightness);
		Bar tightness_ave = new Bar();
		tightness_ave.setName("好友紧密度平均值");
		tightness_ave.setValue(0.1f);
		tightness_ave.setColor(Color.parseColor("#33B5E5"));
		bars_ClusterCoefficient.add(tightness_ave);
		// 真实粉丝率
		Bar truefoll = new Bar();
		truefoll.setName("真实粉丝率");
		truefoll.setValue((float) truefollRatio);
		truefoll.setColor(Color.parseColor("#AA66CC"));
		bars_truefollRatio.add(truefoll);
		Bar truefoll_ave = new Bar();
		truefoll_ave.setName("真实粉丝率平均值");
		truefoll_ave.setValue(0.08f);
		truefoll_ave.setColor(Color.parseColor("#AA66CC"));
		bars_truefollRatio.add(truefoll_ave);
		// 互相关注率
		Bar mutualfocus = new Bar();
		mutualfocus.setName("互相关注率");
		mutualfocus.setValue((float) BilateralRatio);
		mutualfocus.setColor(Color.parseColor("#99CC00"));
		bars_BilateralRatio.add(mutualfocus);
		Bar mutualfocus_ave = new Bar();
		mutualfocus_ave.setName("互相关注率平均值");
		mutualfocus_ave.setValue(0.4f);
		mutualfocus_ave.setColor(Color.parseColor("#99CC00"));
		bars_BilateralRatio.add(mutualfocus_ave);

		// bg.setUnit("百分之");
		bg_ClusterCoefficient.setShowAxis(true);
		bg_ClusterCoefficient.setBars(bars_ClusterCoefficient);

		bg_BilateralRatio.setShowAxis(true);
		bg_BilateralRatio.setBars(bars_BilateralRatio);

		bg_truefollRatio.setShowAxis(true);
		bg_truefollRatio.setBars(bars_truefollRatio);

		// 标题
		TextView title_params = (TextView) view
				.findViewById(R.id.title_userparams);
		title_params.setText(ViewpagerActivity.userName + "的属性参数");
		title_params.setTextColor(Color.parseColor("#A101A6"));

		// 说明部分
		String msg_ClusterCoefficient = null;// 相关说明的字符串形式
		SpannableString msp_ClusterCoefficient = null;
		// 格式设置
		String str_ClusterCoefficient = "\t\t好友紧密度用于衡量该用户的好友相互之间的交互程度，值越高代表用户的好友之间的互动越频繁，关系紧密。";
		msg_ClusterCoefficient = str_ClusterCoefficient;
		msp_ClusterCoefficient = new SpannableString(msg_ClusterCoefficient);
		msp_ClusterCoefficient.setSpan(
				new ForegroundColorSpan(Color.parseColor("#FF6400")), 0,
				0 + str_ClusterCoefficient.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		msp_ClusterCoefficient.setSpan(new RelativeSizeSpan(1.4f), 0,
				str_ClusterCoefficient.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

		TextView text_ClusterCoefficient = (TextView) view
				.findViewById(R.id.text_ClusterCoefficient);
		text_ClusterCoefficient.setText(msp_ClusterCoefficient);
		text_ClusterCoefficient.setTextSize(22);

		// 说明部分
		String msg_BilateralRatio = null;// 相关说明的字符串形式
		SpannableString msp_BilateralRatio = null;
		// 格式设置
		String str_BilateralRatio = "\t\t互相关注率用于衡量用户好友之中互相关注的比例，一般来说，值越高则该用户所关注的人中“亲友团”的比例越大。";
		msg_BilateralRatio = str_BilateralRatio;
		msp_BilateralRatio = new SpannableString(msg_BilateralRatio);
		msp_BilateralRatio.setSpan(
				new ForegroundColorSpan(Color.parseColor("#01939A")), 0,
				0 + str_BilateralRatio.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		msp_BilateralRatio.setSpan(new RelativeSizeSpan(1.4f), 0,
				str_BilateralRatio.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

		TextView text_BilateralRatio = (TextView) view
				.findViewById(R.id.text_BilateralRatio);
		text_BilateralRatio.setText(msp_BilateralRatio);
		text_BilateralRatio.setTextSize(22);

		// 说明部分
		String msg_truefollRatio = null;// 相关说明的字符串形式
		SpannableString msp_truefollRatio = null;
		// 格式设置
		String str_truefollRatio = "\t\t真实粉丝率是衡量一个用户的粉丝质量的标准之一，该值代表了粉丝当中除去“僵尸粉”之外的真实用户的比例，越高则说明粉丝质量越好。";
		msg_truefollRatio = str_truefollRatio;
		msp_truefollRatio = new SpannableString(msg_truefollRatio);
		msp_truefollRatio.setSpan(
				new ForegroundColorSpan(Color.parseColor("#14D100")), 0,
				0 + str_truefollRatio.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		msp_truefollRatio.setSpan(new RelativeSizeSpan(1.4f), 0,
				str_truefollRatio.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

		TextView text_truefollRatio = (TextView) view
				.findViewById(R.id.text_TruefollRatio);
		text_truefollRatio.setText(msp_truefollRatio);
		text_truefollRatio.setTextSize(22);

	}

	// graphview的曲线图设置
	private void SetRepostLine(final View view) throws JSONException {

		final LinearLayout layout = (LinearLayout) view
				.findViewById(R.id.linearlayout);
		Button btn_switchWeibo = (Button) view
				.findViewById(R.id.btn_switchWeibo);
		btn_switchWeibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder builder = new Builder(getActivity());
				String[] items = new String[array_recentlist.length()];
				for (int i = 0; i < array_recentlist.length(); i++) {
					items[i] = "第" + i + "条微博";
				}

				builder.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						weiboNo = which;
						try {
							layout.removeViewAt(1);
							SetRepostLine(view);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				builder.setTitle("选择第几条微博？");
				builder.create().show();
			}
		});

		Button btn_toSpread = (Button) view.findViewById(R.id.btn_toSpread);
		btn_toSpread.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (hasDate) {
					try {
						final String URL = new String(NaviActivity.labURL
								+ "dataMiningResource/gexfs/"
								+ URLEncoder.encode(ViewpagerActivity.userName,
										"utf-8") + "/" + wid + "/1.gexf");
						new Thread(new Runnable() {
							public void run() {
								try {
									// ProgressDialog dialog = new
									// ProgressDialog(getActivity());
									// dialog.setMessage("正在读取，请稍后……");
									FileUtils.byte2File(NetworkUtils
											.get2Server(URL).getBytes("utf-8"),
											getActivity().getExternalCacheDir()
													+ File.separator + "datas",
											"SpreadTemp.gexf");
									String filepath = getActivity()
											.getExternalCacheDir()
											+ File.separator
											+ "datas"
											+ File.separator
											+ "SpreadTemp.gexf";
									Intent intent = new Intent(getActivity(),
											SurfaceViewMain.class);
									Bundle bundle = new Bundle();
									bundle.putString("filepath", filepath);
									bundle.putBoolean("isSpread", true);
									bundle.putString("source", "fragment");
									intent.putExtras(bundle);
									// dialog.dismiss();
									startActivity(intent);
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(), "没有转发图！",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});

		// 根据uid获取最近转发的微博
		// final String recentweibolistURL =
		// "http://10.108.192.119:8080/MicroBlogDisplay/recentweibolist.do?";
		recentweibolist = null;
		JSONObject recentWeibo = (JSONObject) array_recentlist.get(weiboNo);
		wid = recentWeibo.getString("wid");
		TextView text_CurrentWeibo = (TextView) view
				.findViewById(R.id.text_currentWeibo);
		text_CurrentWeibo.setText("微博内容：\n\t" + recentWeibo.getString("Text"));

		JSONArray array_curve = recentWeibo.getJSONArray("repost_timeline");
		// JSONObject testError = array_curve.getJSONObject(0);
		// testError.has("date");
		if (array_curve.length()!=0&&array_curve.getJSONObject(0).has("date")) {
			GraphViewData graphViewDatas[] = new GraphViewData[array_curve
					.length()];
			hasDate = true;
			double Ymax = 0;
			double Xmax = 0;
			for (int i = 0; i < array_curve.length(); i++) {
				JSONObject point = array_curve.getJSONObject(i);
				graphViewDatas[i] = new GraphViewData(point.getInt("date") * 5,
						point.getInt("count"));
				if (point.getInt("count") > Ymax) {
					Ymax = point.getInt("count");
				}
				if (point.getInt("date") * 5 > Xmax) {
					Xmax = point.getInt("date") * 5;
				}
			}

			GraphViewSeries graphViewSeries = new GraphViewSeries(
					graphViewDatas);
			final LineGraphView graphView = new LineGraphView(getActivity(), "");
			graphView.addSeries(graphViewSeries); // data

			graphView.setManualYAxisBounds(Ymax, 0);
			graphView.setManualYAxis(true);
			graphView.setShowHorizontalLabels(true);
			graphView.setDrawDataPoints(true);
			graphView.setScrollable(true);
			graphView.setScalable(true);
			graphView.setViewPort(0, Xmax);
			layout.addView(graphView, 1, new LayoutParams(480, 500));
		} else {
			GraphViewSeries graphViewSeries = new GraphViewSeries(
					new GraphViewData[] { new GraphViewData(0, 0) });
			final LineGraphView graphView = new LineGraphView(getActivity(), "");
			graphView.addSeries(graphViewSeries);
			//
			// graphView.setManualYAxisBounds(Ymax, 0);
			// graphView.setManualYAxis(true);
			// graphView.setShowHorizontalLabels(true);
			// graphView.setDrawDataPoints(true);
			// graphView.setScrollable(true);
			// graphView.setScalable(true);
			// graphView.setViewPort(0, Xmax);
//			getParentFragment().getView().getWidth()*0.8f;
			layout.addView(graphView, 1, new LayoutParams(480, 500));
//			layout.addView(graphView, 1, new LayoutParams((int) (layout.getWidth()*0.6f), 500));
			hasDate = false;
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getActivity(), "没有转发数据！", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
		// 说明部分
		String msg = null;// 相关说明的字符串形式
		SpannableString msp = null;
		TextView text_dscp_postline = (TextView) view
				.findViewById(R.id.text_dscp_postline);
		text_dscp_postline
				.setText("横轴坐标为相对于第一条微博发出后的时间，时间单位分钟，纵轴坐标为当前时间区间的转发次数");
		text_dscp_postline.setTextSize(22);
		text_dscp_postline.setTextColor(Color.parseColor("#33B5E5"));

	}
	// holograph的曲线图设置
	// private void SetLine(View view) {
	// final LineGraph lg = (LineGraph) view.findViewById(R.id.graph_postline);
	// Line l = new Line();
	// LinePoint p = new LinePoint();
	// p.setX(0);
	// p.setY(5);
	// l.addPoint(p);
	// p = new LinePoint();
	// p.setX(8);
	// p.setY(8);
	// l.addPoint(p);
	// p = new LinePoint();
	// p.setX(10);
	// p.setY(4);
	// l.addPoint(p);
	// p = new LinePoint();
	// p.setX(4);
	// p.setY(6);
	// l.addPoint(p);
	// p = new LinePoint();
	// p.setX(2);
	// p.setY(7);
	// l.addPoint(p);
	// p = new LinePoint();
	// p.setX(12);
	// p.setY(3);
	// l.addPoint(p);
	// p = new LinePoint();
	// p.setX(14);
	// p.setY(9);
	// l.addPoint(p);
	// l.setColor(Color.parseColor("#FFBB33"));
	// l.setShowingPoints(true);
	// lg.addLine(l);
	// lg.setRangeY(0, 10);
	//
	// lg.setUsingDips(true);
	// lg.setLineToFill(0);
	//
	// lg.setOnPointClickedListener(new OnPointClickedListener() {
	//
	// @Override
	// public void onClick(int lineIndex, int pointIndex) {
	// // TODO Auto-generated method stub
	// Toast.makeText(getActivity(),
	// lg.getLine(lineIndex).getPoint(pointIndex).toString(),
	// Toast.LENGTH_SHORT).show();
	// ;
	// }
	// });

	// // 说明部分
	// String msg = null;// 相关说明的字符串形式
	// SpannableString msp = null;
	// // 格式设置
	// // String strpeak = lg.getLine(0).getPoint(3).toString();
	// msg = "您近期的微博转发高峰在：" + strpeak;
	// msp = new SpannableString(msg);
	// msp.setSpan(new ForegroundColorSpan(Color.parseColor("#AA66CC")), 12,
	// 12 + strpeak.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
	// msp.setSpan(new RelativeSizeSpan(1.4f), 12, 12 + strpeak.length(),
	// Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
	//
	// TextView text_dscp_postline = (TextView) view
	// .findViewById(R.id.text_dscp_postline);
	// text_dscp_postline.setText(msp);
	// text_dscp_postline.setTextSize(22);
	//
	// }

}
