package effect.cheng.service;

import java.math.BigDecimal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

public class TrafficService {
	Context context;
	ConnectivityManager cm;
	NetworkInfo nwi;
	long lastTraffic = 0;
	long currentTraffic;

	// ���캯��
	public TrafficService() {
	}

	public TrafficService(Context context) {
		this.context = context;
		cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		nwi = cm.getActiveNetworkInfo();
	}

	// ��ȡ��ǰ�ֻ����������ͣ�����String
	public int getNetType() {
		if (nwi != null) {
			String net = nwi.getTypeName();
			if (net.equals("WIFI")) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return -1;
		}
	}

	// ��ѯ�ֻ�������
	public static long traffic_Monitoring() {
		long recive_Total = TrafficStats.getTotalRxBytes();
		long send_Total = TrafficStats.getTotalTxBytes();
		long total = recive_Total + send_Total;
		return total;
	}

	// ��ѯ�ֻ���Mobile��������
	public static long mReceive() {
		return TrafficStats.getMobileRxBytes();
	}

	// ��ѯ�ֻ���Mobile��������
	public static long mSend() {
		return TrafficStats.getMobileTxBytes();
	}

	// ��ѯ�ֻ���WIFI��������
	public static long wSend() {
		return TrafficStats.getTotalTxBytes() - TrafficStats.getMobileTxBytes();
	}

	// ��ѯ�ֻ�Wifi����������
	public static long wReceive() {
		return TrafficStats.getTotalTxBytes() - TrafficStats.getMobileRxBytes();
	}

	// ��ѯĳ��Uid������ֵ
	public static long monitoringEachApplicationReceive(int uid) {
		return TrafficStats.getUidRxBytes(uid);
	}

	// ��ѯĳ��Uid������ֵ
	public static long monitoringEachApplicationSend(int uid) {
		return TrafficStats.getUidTxBytes(uid);
	}

	// ����ת��
	public static String convert(long traffic) {
		BigDecimal kb;
		BigDecimal mb;
		BigDecimal gb;

		BigDecimal temp = new BigDecimal(traffic);
		BigDecimal divide = new BigDecimal(1024);
		kb = temp.divide(divide, 2, 1);
		if (kb.compareTo(divide) > 0) {
			mb = kb.divide(divide, 2, 1);
			if (mb.compareTo(divide) > 0) {
				gb = mb.divide(divide, 2, 1);
				return gb.doubleValue() + "GB";
			} else {
				return mb.doubleValue() + "MB";
			}
		} else {
			return kb.doubleValue() + "KB";
		}
	}
}
