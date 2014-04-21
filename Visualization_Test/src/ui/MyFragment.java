package ui;

import java.text.DecimalFormat;
import java.util.ArrayList;

import jayvee.visualization_test.R;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieGraph.OnSliceClickedListener;
import com.echo.holographlibrary.PieSlice;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment {
	int mNum;// 页号

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

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);
		// 此处可以给每个page对应一个独立的布局
		System.out.println("oncreateview");
		View view;
		mNum = getArguments() != null ? mNum = getArguments().getInt("viewNum")
				: 0;
		switch (mNum) {
		case 0:// 测试中，0为饼图
			view = inflater.inflate(R.layout.fragment_sex, container, false);
			SetPie(view);
			return view;
		case 1:// 测试中，1为曲线图
			view = inflater.inflate(R.layout.fragment_postline, container,
					false);
			SetLine(view);
			return view;
		case 2:// 测试中，2为柱状图
			view = inflater.inflate(R.layout.fragment_userparams, container,
					false);
			SetBar(view);
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
		// TextView textView = (TextView) view.findViewById(R.id.section_label);
		// switch (mNum) {
		// case 0:// 测试中，0为饼图
		// // textView.setText("第一页");
		// SetPie(view);
		// break;
		// case 1:// 测试中，1为线图
		// // textView.setText("第二页");
		// SetLine(view);
		// break;
		// case 2:// 测试中，2为柱状图
		// // textView.setText("第三页");
		// SetBar(view);
		// break;
		// default:
		// // textView.setText("默认页");
		// SetPie(view);
		// }
	}

	private void SetPie(View view) {
		final PieGraph pg = (PieGraph) view.findViewById(R.id.graph_sex);
		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#33B5E5"));
		slice.setValue(23);
		slice.setTitle("男生");
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#FF4444"));
		slice.setValue(41);
		slice.setTitle("女生");
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
				// TODO Auto-generated method stub
				// Toast.makeText(getActivity(), pg.getSlice(index).getTitle(),
				// Toast.LENGTH_SHORT).show();
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
		String strboy = "您的粉丝当中" + "男生" + "共有" + countboy + "位,占"
				+ fnum.format(percentboy) + "%\n";
		String strgirl = "女生" + "共有" + countgirl + "位,占"
				+ fnum.format(percentgirl) + "%\n";
		String strcomment = "嗯您的女生缘很强！";

		String msg = null;// 相关说明的字符串形式
		SpannableString msp = null;
		msg = strboy + strgirl + strcomment;
		// msg = "您的粉丝当中" + "男生" + "共有" + countboy + "位,占"
		// + fnum.format(percentboy) + "%\n" + "女生" + "共有" + countgirl
		// + "位,占" + fnum.format(percentgirl) + "%\n";

		// 格式设置
		msp = new SpannableString(msg);
		// 男生
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(0).getColor()), 6, 8,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.2f), 6, 8,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 男生个数
		msp.setSpan(new ForegroundColorSpan(pg.getSlice(0).getColor()), 10,
				10 + String.valueOf(countboy).length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.5f), 10,
				10 + String.valueOf(countboy).length(),
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

	private void SetBar(View view) {
		final BarGraph bg = (BarGraph) view.findViewById(R.id.graph_userparams);
		ArrayList<Bar> bars = new ArrayList<Bar>();
		// 好友紧密度
		Bar tightness = new Bar();
		tightness.setName("好友紧密度");
		tightness.setValue(0.0342f);
		tightness.setColor(Color.parseColor("#33B5E5"));
		tightness.setAveValue(0.05f);
		bars.add(tightness);
		// 真实粉丝率
		Bar truefoll = new Bar();
		truefoll.setName("真实粉丝率");
		truefoll.setValue(0.4342f);
		truefoll.setColor(Color.parseColor("#AA66CC"));
		truefoll.setAveValue(0.63f);
		bars.add(truefoll);
		// 互相关注率
		Bar mutualfocus = new Bar();
		mutualfocus.setName("互相关注率");
		mutualfocus.setValue(0.8342f);
		mutualfocus.setColor(Color.parseColor("#99CC00"));
		mutualfocus.setAveValue(0.42f);
		bars.add(mutualfocus);

		// bg.setUnit("百分之");
		bg.setShowAxis(true);
		bg.setBars(bars);

		// 说明部分
		String msg = null;// 相关说明的字符串形式
		SpannableString msp = null;
		// 格式设置
		String strtest = "以上是您账号的粉丝相关参数";
		msg = strtest;
		msp = new SpannableString(msg);
		msp.setSpan(new ForegroundColorSpan(Color.parseColor("#FF8800")), 0,
				0 + strtest.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.4f), 0, strtest.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

		TextView text_dscp_userparams = (TextView) view
				.findViewById(R.id.text_dscp_userparams);
		text_dscp_userparams.setText(msp);
		text_dscp_userparams.setTextSize(22);

	}

	private void SetLine(View view) {
		final LineGraph lg = (LineGraph) view.findViewById(R.id.graph_postline);
		Line l = new Line();
		LinePoint p = new LinePoint();
		p.setX(0);
		p.setY(5);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(8);
		p.setY(8);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(10);
		p.setY(4);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(4);
		p.setY(6);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(2);
		p.setY(7);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(12);
		p.setY(3);
		l.addPoint(p);
		p = new LinePoint();
		p.setX(14);
		p.setY(9);
		l.addPoint(p);
		l.setColor(Color.parseColor("#FFBB33"));
		l.setShowingPoints(true);
		lg.addLine(l);
		lg.setRangeY(0, 10);

		lg.setUsingDips(true);
		lg.setLineToFill(0);

		lg.setOnPointClickedListener(new OnPointClickedListener() {

			@Override
			public void onClick(int lineIndex, int pointIndex) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(),
						lg.getLine(lineIndex).getPoint(pointIndex).toString(),
						Toast.LENGTH_SHORT).show();
				;
			}
		});

		// 说明部分
		String msg = null;// 相关说明的字符串形式
		SpannableString msp = null;
		// 格式设置
		String strpeak = lg.getLine(0).getPoint(3).toString();
		msg = "您近期的微博转发高峰在：" + strpeak;
		msp = new SpannableString(msg);
		msp.setSpan(new ForegroundColorSpan(Color.parseColor("#AA66CC")), 12,
				12 + strpeak.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		msp.setSpan(new RelativeSizeSpan(1.4f), 12, 12 + strpeak.length(),
				Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

		TextView text_dscp_postline = (TextView) view
				.findViewById(R.id.text_dscp_postline);
		text_dscp_postline.setText(msp);
		text_dscp_postline.setTextSize(22);

	}

}
