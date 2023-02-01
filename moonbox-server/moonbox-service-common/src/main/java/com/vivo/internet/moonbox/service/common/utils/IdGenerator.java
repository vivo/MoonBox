/**
 * Copyright:©1985-2020 vivo Communication Technology Co., Ltd. All rights reserved.
 *
 * @Company: 维沃移动通信有限公司
 */
/*
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
