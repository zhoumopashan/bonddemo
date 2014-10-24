package com.example.bonddemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {

	private EditText etStartTime;
	private EditText etEndTime;
	private Button mBtn;

	private EditText mEdrv;
	private EditText mEdr;
	private EditText mEdrt;
	private EditText mEdDa1;
	private EditText mEdDa2;
	private EditText mPri;
	private TextView mEdr2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
	}


	private void initViews() {
		etStartTime = (EditText) this.findViewById(R.id.bond_data1);
		etEndTime = (EditText) this.findViewById(R.id.bond_data2);

		etStartTime.setOnTouchListener(this);
		etEndTime.setOnTouchListener(this);

		mEdrv = (EditText) this.findViewById(R.id.bond_rv);
		mEdr = (EditText) this.findViewById(R.id.bond_r);
		mEdrt = (EditText) this.findViewById(R.id.bond_times);
		mPri = (EditText) this.findViewById(R.id.bond_pri);
		mEdr2 = (TextView) this.findViewById(R.id.bond_r2);

		mBtn = (Button) findViewById(R.id.go);
		mBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				double d1 = Double.valueOf(mEdrv.getText().toString());
				double d2 = Double.valueOf(mEdr.getText().toString()) / 100;
				double d3 = Double.valueOf(mEdrt.getText().toString());
				double dPri = Double.valueOf(mPri.getText().toString());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date data1;
				Date data2;
				try {
					Log.e("luo", "etStartTime:" + etStartTime.getText().toString());
					data1 = (Date) sdf.parse(etStartTime.getText().toString());
					data2 = (Date) sdf.parse(etEndTime.getText().toString());
				} catch (ParseException e) {
					Toast.makeText(MainActivity.this, "日期格式有问题", Toast.LENGTH_LONG).show();
					return;
				}

				double d6 = calcY(d1, d2, d3, dPri, data1, data2, 15);
				d6 = d6 * 100;
						
			    NumberFormat v0 = DecimalFormat.getInstance();
	            ((DecimalFormat)v0).setRoundingMode(RoundingMode.HALF_UP);
	            ((DecimalFormat)v0).setMaximumFractionDigits(2);
	            ((DecimalFormat)v0).setMinimumFractionDigits(2);
	            mEdr2.setText(((DecimalFormat)v0).format(d6) + "%");

//				mEdr2.setText("" + d6);
				Log.e("luo", "d1:" + d1);
				Log.e("luo", "d2:" + d2);
				Log.e("luo", "d3:" + d3);
				Log.e("luo", "data1:" + data1);
				Log.e("luo", "data2:" + data2);
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = View.inflate(this, R.layout.date_dialog, null);
			final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
			builder.setView(view);

			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

			if (v.getId() == R.id.bond_data1) {
				final int inType = etStartTime.getInputType();
				etStartTime.setInputType(InputType.TYPE_NULL);
				etStartTime.onTouchEvent(event);
				etStartTime.setInputType(inType);
				etStartTime.setSelection(etStartTime.getText().length());

				builder.setTitle("选取起始时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));

						etStartTime.setText(sb);
						etEndTime.requestFocus();

						dialog.cancel();
					}
				});

			} else if (v.getId() == R.id.bond_data2) {
				int inType = etEndTime.getInputType();
				etEndTime.setInputType(InputType.TYPE_NULL);
				etEndTime.onTouchEvent(event);
				etEndTime.setInputType(inType);
				etEndTime.setSelection(etEndTime.getText().length());

				builder.setTitle("选取结束时间");
				builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						StringBuffer sb = new StringBuffer();
						sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
						etEndTime.setText(sb);

						dialog.cancel();
					}
				});
			}

			Dialog dialog = builder.create();
			dialog.show();
		}

		return true;
	}

	private double calcY(double paramDouble1, double paramDouble2, double paramDouble3, double price, Date paramDate1, Date paramDate2, int paramInt) {

		BigDecimal localBigDecimal1 = new BigDecimal(Double.toString(0.05D));
		BigDecimal localBigDecimal2 = new BigDecimal(Double.toString(0.01D));

		Double localDouble1 = null;
		Double localDouble2 = null;

		double d1 = 0.0D;
		double d2 = 0.0D;

		int i = 0;
		int j = 0;
		do {
			double d3 = calcPRI(paramDouble1, paramDouble2, paramDouble3, localBigDecimal1.doubleValue(), paramDate1, paramDate2);

			if (d3 == price)
				return localBigDecimal1.doubleValue();

			if (d3 > price) {
				localDouble1 = Double.valueOf(d3);
				d1 = localBigDecimal1.doubleValue();
				localBigDecimal1 = localBigDecimal1.add(localBigDecimal2);
			} else {
				localDouble2 = Double.valueOf(d3);
				d2 = localBigDecimal1.doubleValue();
				localBigDecimal1 = localBigDecimal1.subtract(localBigDecimal2);
			}

			if (localDouble1 != null && localDouble2 != null) {
				localBigDecimal1 = new BigDecimal(Double.toString((d2 + d1) / 2D));
				localBigDecimal2 = new BigDecimal(Double.toString(0.01D / 10D));
				++i;
				if (i == paramInt) {
					if (price - localDouble2.doubleValue() <= localDouble1.doubleValue() - price) {
						return d2;
					} else {
						return d1;
					}
				}
			}

			j++;

		} while (j != 200);

		return 0;
	}

	private double calcPRI(double rv, double r, double m, double y, Date startTime, Date endTime) {
		int[] v8 = calcEA(m, startTime, endTime);
		double v4 = ((double) v8[0]);
		double v9 = ((double) v8[1]);
		double v17 = ((double) v8[2]);
		double v6 = v4 / v9;
		double v15 = 1 + y / m;
		double v19 = rv / Math.pow(v15, v17 - v6);
		double v13 = 100 * r / m;
		double v11 = 1;
		int v21 = 0;

		while ((((double) v21)) < v17) {
			v19 += v13 / Math.pow(v15, v11 - v6);
			++v11;
			++v21;
		}

		return v19 - v13 * v6;
	}

	private int[] calcEA(double m, Date startTime, Date endTime) {
		int v1;
		int v14;
		Calendar v12 = Calendar.getInstance();
		v12.setTime(startTime);
		Calendar v6 = Calendar.getInstance();
		v6.setTime(endTime);
		int v13 = v6.get(1) - v12.get(1);
		int v9 = v6.get(2) + 1 + (11 - v12.get(2));
		if (v13 > 0) {
			v14 = v13 - 1;
		} else {
			v14 = 0;
		}

		int v10 = v14 * 12 + v9;
		int v8 = ((int) (12 / m));
		int v4 = v10 % v8;
		int v11 = ((int) Math.ceil(((double) (v10 / v8))));
		int v7 = 0;
		if (v4 == 0) {
			if (v6.get(5) < v12.get(5)) {
				v4 = v8;
				--v11;
			} else if (v6.get(5) == v12.get(5)) {
				v7 = 1;
			}
		}

		Calendar v2 = Calendar.getInstance();
		v9 = v12.get(2) + v4;
		v13 = v12.get(1);
		if (v9 > 11) {
			++v13;
			v9 += -12;
		}

		v2.set(v13, v9, v6.get(5));
		Calendar v3 = Calendar.getInstance();
		v9 = v2.get(2) - v8;
		v13 = v2.get(1);
		if (v9 < 0) {
			--v13;
			v9 += 12;
		}

		v3.set(v13, v9, v2.get(5));
		int v5 = calcDays(v3.getTime(), v2.getTime());
		if (v7 != 0) {
			v1 = 0;
		} else {
			v1 = calcDays(v3.getTime(), startTime) + 1;
		}

		if (v7 == 0) {
			++v11;
		}

		return new int[] { v1, v5, v11 };
	}

	private int calcDays(Date start, Date end) {
		return (int) ((end.getTime() - start.getTime()) / 86400000L);
	}
}
