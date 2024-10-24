package com.amnex.agristack.utils;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.producer.NumbersAnswerProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;

public class CaptchaService {
	//Creating Captcha Object
		public static Captcha createCaptcha(Integer width, Integer height) {
			char[] DEFAULT_CHARS = new char[] { 'a', 'b', 'c', 'd',
		            'e', 'f', 'g', 'h', 'i','j','k','l', 'm', 'n','o', 'p','q', 'r','s','t','u','v', 'w', 'x', 'y','z',
		            '0','1','2', '3', '4', '5', '6', '7', '8','9','!','@','#','$','%','^','&','*' };
			return new Captcha.Builder(width, height)
					.addBackground(new GradiatedBackgroundProducer())
//					.addText(new NumbersAnswerProducer(6)) 
					.addText(new DefaultTextProducer(6,DEFAULT_CHARS), new DefaultWordRenderer())
					.addNoise(new CurvedLineNoiseProducer())
					.build();
		}
		
		//Converting to binary String
		public static String encodeCaptcha(Captcha captcha) {
			String image = null;
			try {
				ByteArrayOutputStream bos= new ByteArrayOutputStream();
				ImageIO.write(captcha.getImage(),"jpg", bos);
				byte[] byteArray= Base64.getEncoder().encode(bos.toByteArray());
				image = new String(byteArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return image;
		}
}
