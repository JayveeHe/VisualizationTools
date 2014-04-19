package ui;

import java.util.ArrayList;

import jayvee.visualization_test.R;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieGraph.OnSliceClickedListener;
import com.echo.holographlibrary.PieSlice;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		slice.setColor(Color.parseColor("#99CC00"));
		slice.setValue(2);
		slice.setTitle("第一个");
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#FFBB33"));
		slice.setValue(3);
		slice.setTitle("第二个");
		pg.addSlice(slice);
		// slice = new PieSlice();
		// slice.setColor(Color.parseColor("#AA66CC"));
		// slice.setValue(8);
		// slice.setTitle("第三个");
		// pg.addSlice(slice);

		pg.setInnerCircleRatio(100);

		pg.setOnSliceClickedListener(new OnSliceClickedListener() {

			@Override
			public void onClick(int index) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), pg.getSlice(index).getTitle(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void SetBar(View view) {
		final BarGraph bg = (BarGraph) view.findViewById(R.id.graph_userparams);
		ArrayList<Bar> bars = new ArrayList<Bar>();
		// 好友紧密度
		Bar tightness = new Bar();
		tightness.setName("好友紧密度");
		tightness.setValue(0.0342f);
		tightness.setColor(Color.parseColor("#33B5E5"));
		bars.add(tightness);
		// 真实粉丝率
		Bar truefoll = new Bar();
		truefoll.setName("真实粉丝率");
		truefoll.setValue(0.4342f);
		truefoll.setColor(Color.parseColor("#AA66CC"));
		bars.add(truefoll);
		// 互相关注率
		Bar mutualfocus = new Bar();
		mutualfocus.setName("互相关注率");
		mutualfocus.setValue(0.8342f);
		mutualfocus.setColor(Color.parseColor("#99CC00"));
		bars.add(mutualfocus);

		// bg.setUnit("百分之");
		bg.setBars(bars);
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
		l.setColor(Color.parseColor("#FFBB33"));
		l.setShowingPoints(true);
		lg.addLine(l);
		lg.setRangeY(0, 10);
		lg.setLineToFill(0);
	}

}
