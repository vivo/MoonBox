/**
 * Copyright:©1985-2020 vivo Communication Technology Co., Ltd. All rights reserved.
 *
 * @Company: 维沃移动通信有限公司
 */
package com.vivo.internet.moonbox.service.common.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;



/**
 * ConverterSupported - {@link IdGenerator}
 *
 * @author yanjiang.liu
 * @version 1.0
 * @since 2022/8/30 19:28
 */
public class IdGenerator {

	private static final JdkIdGenerator generator = new JdkIdGenerator();

	public static String templateId() {
		return "tm_id_"+innerReplace(generator.generateId().toString());
	}

	public static String uuid() {
		return innerReplace(generator.generateId().toString());
	}

	public static String recordTaskRunId() {
		return "rc_id_"+innerReplace(generator.generateId().toString());
	}

	public static String replayTaskRunId() {
		return "rp_id_"+innerReplace(generator.generateId().toString());
	}


	private static String innerReplace(String data) {
		return data.replace("-","");
	}

	private static class JdkIdGenerator {

		private final Random random;


		public JdkIdGenerator() {
			SecureRandom secureRandom = new SecureRandom();
			byte[] seed = new byte[8];
			secureRandom.nextBytes(seed);
			this.random = new Random(new BigInteger(seed).longValue());
		}

		public UUID generateId() {
			byte[] randomBytes = new byte[16];
			this.random.nextBytes(randomBytes);

			long mostSigBits = 0;
			for (int i = 0; i < 8; i++) {
				mostSigBits = (mostSigBits << 8) | (randomBytes[i] & 0xff);
			}

			long leastSigBits = 0;
			for (int i = 8; i < 16; i++) {
				leastSigBits = (leastSigBits << 8) | (randomBytes[i] & 0xff);
			}

			return new UUID(mostSigBits, leastSigBits);
		}

	}

}
