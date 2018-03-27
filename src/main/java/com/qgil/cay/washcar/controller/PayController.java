package com.qgil.cay.washcar.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qgil.cay.washcar.entity.Bill;
import com.qgil.cay.washcar.entity.PayApply;
import com.qgil.cay.washcar.entity.PayConst;
import com.qgil.cay.washcar.entity.PayResult;
import com.qgil.cay.washcar.entity.PayResultEntity;
import com.qgil.cay.washcar.entity.PushConfig;
import com.qgil.cay.washcar.entity.PushExtra;
import com.qgil.cay.washcar.entity.QrcodeResult;
import com.qgil.cay.washcar.entity.QrcodeResultEntity;
import com.qgil.cay.washcar.service.PayServices;
import com.qgil.cay.washcar.utils.AESUtil;

@Controller
public class PayController {
	@Autowired
	private PushConfig pushConfig;
	@GetMapping("/getQrCode")
	@ResponseBody
	public QrcodeResultEntity getQrCode(@RequestParam(value="dataid", required = false, defaultValue = "") String dataid) {
		QrcodeResult res = new QrcodeResult();
		QrcodeResultEntity result = new QrcodeResultEntity();
//		PayServices pay = new PayServices();
//        Bill bill = pay.uploadBill();
//        String qrcode = pay.payApply(bill.getOrderno(), "alipay".equals(dataid)?PayConst.PAYCHAN_ALIPAY:PayConst.PAYCHAN_WECHAT);
//        if ("alipay".equals(dataid)) {
//        	res.setAli_qrcode(qrcode);
//        } else {
//        	res.setWx_qrcode(qrcode);
//        }
        res.setAli_qrcode("fuckyou");
		res.setWx_qrcode("http://www.baidu.com/");
        result.setOk();
        result.setQrcoderesult(res);
        result.setOrderid("orderid");
        return result;
	}
	
	@GetMapping("/getresult")
	@ResponseBody
	public PayResultEntity getResult(HttpServletRequest request, HttpServletResponse response) {
		PayResultEntity res = new PayResultEntity();
		PayResult payresult = new PayResult();
//		String params = request.getParameter("test");
//		String content = AESUtil.decrypt(params, PayConst.SECRET_KEY);
//		payresult = JSONObject.parseObject(content, PayResult.class);
//		res.setPayresult(payresult);
		
		PushController push = new PushController();
    	List<PushExtra> extralist = new ArrayList<PushExtra>();
        extralist.add(new PushExtra("id","orderid"));
        push.pushMessage("", "新的信息评论", "新消息", JSONArray.toJSONString(extralist),pushConfig.getAppKey(),pushConfig.getMasterSecret());
        res.setOk();
        return res;
	}
}
