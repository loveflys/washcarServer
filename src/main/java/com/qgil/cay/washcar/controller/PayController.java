package com.qgil.cay.washcar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qgil.cay.washcar.entity.Bill;
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
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private PushConfig pushConfig;
	@GetMapping("/getQrCode")
	@ResponseBody
	public QrcodeResultEntity getQrCode(@RequestParam(value="dataid", required = false, defaultValue = "") String dataid) {
		QrcodeResult res = new QrcodeResult();
		QrcodeResultEntity result = new QrcodeResultEntity();
		PayServices pay = new PayServices();
		log.info("生成订单start===========");
        Bill bill = pay.uploadBill();
        log.info("生成订单====orderNo======="+bill.getOrderno());
        log.info("生成订单====标题======="+bill.getBilltitle());
        log.info("生成订单====时间======="+Calendar.getInstance().getTime().toLocaleString());
        log.info("生成订单end===========");
        log.info("申请支付获取二维码start===========");
        String qrcode = pay.payApply(bill.getOrderno(), "alipay".equals(dataid)?PayConst.PAYCHAN_ALIPAY:PayConst.PAYCHAN_WECHAT);
        log.info("申请支付获取二维码result==>" + qrcode);
        log.info("申请支付获取二维码end===========");
        if ("alipay".equals(dataid)) {
        	res.setAli_qrcode(qrcode);
        } else {
        	res.setWx_qrcode(qrcode);
        }
        result.setOk();
        result.setQrcoderesult(res);
        result.setOrderid(bill.getOrderno());
        result.setFee(PayConst.WASHCAR_COST);
        return result;
	}
	
	@PostMapping("/getresult")
	@ResponseBody
	public PayResultEntity getResult(HttpServletRequest request, HttpServletResponse response) {
		PayResultEntity res = new PayResultEntity();
		PayResult payresult = new PayResult();
		
		try {
			byte[] test = AESUtil.readStream(request.getInputStream());
			String tempContent = new String(test);
			String content = AESUtil.decrypt(tempContent, PayConst.SECRET_KEY);
			log.info("支付回调==>" + ("OR02".equals(payresult.getOrderSts())?"支付成功":"支付失败"));
			log.info("支付回调返回信息==>" + content);
			payresult = JSONObject.parseObject(content, PayResult.class);
			res.setPayresult(payresult);
			
			PushController push = new PushController();
	    	List<PushExtra> extralist = new ArrayList<PushExtra>();
	        extralist.add(new PushExtra("id",payresult.getOrderNo()));
	        push.pushMessage("OR02".equals(payresult.getOrderSts())?"支付成功":"支付失败", "支付结果通知", content, JSONArray.toJSONString(extralist),pushConfig.getAppKey(),pushConfig.getMasterSecret());
	        res.setOk();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
        return res;
	}
}
