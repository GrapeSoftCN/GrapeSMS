package interfaceApplication;

import org.json.simple.JSONObject;

import esayhelper.JSONHelper;
import esayhelper.TimeHelper;
import esayhelper.jGrapeFW_Message;
import io.netty.handler.codec.http.HttpContentEncoder.Result;
import session.session;
import sms.ruoyaMASDB;

public class SMS {
	// 发送处理件短信至举报人
	// private void SendSMS(String id) {
	// String message = SearchById(id);
	// String msg = JSONHelper.string2json(message).get("message").toString();
	// JSONObject object = JSONHelper.string2json(msg);
	// if (object.containsKey("InformantPhone")) {
	// String phone = object.get("InformantPhone").toString();
	// String text = "";
	// if ("3".equals(object.get("state").toString())) {
	// text = "您举报的" + object.get("content").toString() + "已被拒绝,"
	// + "拒绝理由为：" + object.get("reason").toString();
	// }
	// if ("2".equals(object.get("state").toString())) {
	// text = "您举报的" + object.get("content").toString() + "已处理完成";
	// }
	// ruoyaMASDB.sendSMS(phone, text);
	// }
	// }

	// 发送验证短信
	@SuppressWarnings("unchecked")
	public String SendVerity(String phone, String text) {
		session session = new session();
		String time = "";
		String currenttime = TimeHelper.stampToDate(TimeHelper.nowMillis())
				.split(" ")[0];
		int count = 0;
		JSONObject object = new JSONObject();
		if (session.get(phone) != null) {
			System.out.println(session.get(phone).toString());
			object = JSONHelper.string2json(session.get(phone).toString());
			count = Integer.parseInt(object.get("count").toString()); // 次数
			time = TimeHelper
					.stampToDate(Long.parseLong(object.get("time").toString()));
			time = time.split(" ")[0];
			if (currenttime.equals(time) && count == 5) {
				return resultMessage(1);
			}
		}
		String tip = ruoyaMASDB.sendSMS(phone, text);
		count++;
		object.put("count", count + "");
		object.put("time", TimeHelper.nowMillis());
		session.setget(phone, object);
		return tip != null ? resultMessage(0) : resultMessage(99);
	}

	private String resultMessage(int num) {
		return resultMessage(num, "");
	}

	public String resultMessage(int num, String message) {
		String msg = "";
		switch (num) {
		case 0:
			msg = message;
			break;
		case 1:
			msg = "您今日短信发送次数已达上线";
			break;
		default:
			msg = "其他操作异常";
			break;
		}
		return jGrapeFW_Message.netMSG(num, msg);
	}
}
