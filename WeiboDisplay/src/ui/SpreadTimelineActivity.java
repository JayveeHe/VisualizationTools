package ui;

import java.text.ParseException;
import java.util.ArrayList;

import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import jayvee.visualization_weibo.R;
import Nodes.SpreadNodeData;
import SpreadUtils.WeiboSpreadUtils;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SpreadTimelineActivity extends Activity {
	public static int[] curve;
	public static String weiboText;
	public static long startTime;
	public static ArrayList<SpreadNodeData> repostRank;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_spreadtimeline);
		GraphViewData graphViewDatas[] = new GraphViewData[curve.length];
		double Ymax = 0;
		double Xmax = 0;
		for (int i = 0; i < curve.length; i++) {
			graphViewDatas[i] = new GraphViewData(i * 5, curve[i]);
			if (curve[i] > Ymax) {
				Ymax = curve[i];
			}
			if (i * 5 > Xmax) {
				Xmax = i * 5;
			}
		}

		GraphViewSeries graphViewSeries = new GraphViewSeries(graphViewDatas);
		final LineGraphView graphView = new LineGraphView(this, "");
		graphView.addSeries(graphViewSeries); // data

		// graphView.setHorizontalLabels(horlabels);
		graphView.setManualYAxisBounds(Ymax, 0);
		graphView.setManualYAxis(true);
		graphView.setShowHorizontalLabels(true);
		graphView.setDrawDataPoints(true);
		graphView.setScrollable(true);
		graphView.setScalable(true);
		graphView.setViewPort(0, Xmax);
		// layout.addView(graphView);
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout_spreadtimeline);
		// layout.addView(graphView, 1, new LayoutParams(480, 500));
		layout.addView(graphView, 1, new LayoutParams(
				(int) (NaviActivity.ViewWidth * 0.9f),
				(int) (NaviActivity.ViewHeight * 0.5f)));

		// 标注点的处理
		GraphViewData clickData = new GraphViewData(0, curve[0]);
		GraphViewSeries gvs = new GraphViewSeries(
				new GraphViewData[] { clickData });
		graphView.addSeries(gvs);

		// 说明部分
		String msg = null;// 相关说明的字符串形式
		SpannableString msp = null;
		// 格式设置
		TextView text_dscp_postline = (TextView) findViewById(R.id.text_dscp_spreadTimeline);
		text_dscp_postline
				.setText("横轴坐标为相对于第一条微博发出后的时间，时间单位分钟，纵轴坐标为当前时间区间的转发次数");
		text_dscp_postline.setTextSize(22);
		text_dscp_postline.setTextColor(Color.parseColor("#33B5E5"));
		final TextView text_clickname = (TextView) findViewById(R.id.text_clickname);
		// text_clickname.setVisibility(TextView.GONE);
		TextView text_CurrentWeibo = (TextView) findViewById(R.id.text_spreadCurrentWeibo);
		text_CurrentWeibo.setText("微博内容：\n\t" + weiboText);

		MyListView list_rank = (MyListView) findViewById(R.id.list_repostRank);
		list_rank.setAdapter(new MyRanklistAdapter());
		OnItemClickListener MyRankItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// graphView.setViewPort(repostRank.get(position).getPost_time(),
				// size);
				long selectedTime = 0;
				try {
					selectedTime = WeiboSpreadUtils.parseTime(repostRank.get(
							position).getPost_time());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final float Xposition = (float) Math
						.floor((selectedTime - startTime) / (60000));
				GraphViewSeriesStyle gvss = new GraphViewSeriesStyle();
				int lastX = (int) Math.floor(Xposition / 5);
				int nextX = lastX + 1;
				final float clickY;
				if (curve.length <= 1) {
					clickY = curve[0];
				} else {
					clickY = curve[lastX] + ((Xposition - lastX * 5) / 5)
							* (curve[nextX] - curve[lastX]);
				}
				// System.out.println("lastX="+lastX+"="+curve[lastX]+"nextX="+nextX+"="+curve[nextX]);
				GraphViewData clickData = new GraphViewData(Xposition, clickY);
				gvss.color = Color.parseColor("#ff0000");
				GraphViewSeries gvs = new GraphViewSeries("点击到的点", gvss,
						new GraphViewData[] { clickData });
				graphView.removeSeries(1);
				graphView.addSeries(gvs);
				text_clickname.setVisibility(TextView.VISIBLE);
				// text_clickname.setTextColor(Color.parseColor("#ff0000"));
				text_clickname.setText("当前标注用户："
						+ repostRank.get(position).getName());
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(SpreadTimelineActivity.this,
								"距离第一条转发的时间：" + Xposition + "分钟",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		};
		list_rank.setOnItemClickListener(MyRankItemClickListener);
	}

	class MyRanklistAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return repostRank.size() > 10 ? 10 : repostRank.size();
		}

		@Override
		public Object getItem(int arg0) {
			return repostRank.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return Long.parseLong(repostRank.get(arg0).getWid());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			// 获取list的item对象
			if (null == convertView) {
				convertView = View.inflate(SpreadTimelineActivity.this,
						R.layout.list_rankitem, null);
				holder = new ViewHolder();
				holder.text_rank = (TextView) convertView
						.findViewById(R.id.text_rank);
				holder.text_rank_name = (TextView) convertView
						.findViewById(R.id.text_rank_name);
				holder.text_rank_time = (TextView) convertView
						.findViewById(R.id.text_rank_time);
				holder.text_rank_count = (TextView) convertView
						.findViewById(R.id.text_rank_count);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text_rank.setText("第" + (position + 1) + "名");
			holder.text_rank_name.setText(repostRank.get(position).getName());
			holder.text_rank_time.setText("发送时间："
					+ repostRank.get(position).getPost_time());
			holder.text_rank_count.setText(repostRank.get(position)
					.getRepost_count() + "");
			return convertView;
		}
	}

	private final class ViewHolder {
		public TextView text_rank;
		public TextView text_rank_name;
		public TextView text_rank_time;
		public TextView text_rank_count;
	}
}
