/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.qiji.live.xiaozhibo.alipay;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	//合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088411851435855";

	//收款支付宝账号
	public static final String DEFAULT_SELLER = "zqm@taianweb.com";

	//商户私钥，自助生成没有转换的RSA私钥
	//public static final String PRIVATE = "MIICWwIBAAKBgQCz+NhxSpBeKuXYaWr/ceYkseShAqmp/ETQG8AEiSlKO47hfw5kH+wyvp6QVaAvhF1K9ME5HwqOz8G5mgGWXDVy3WTkyrPZlm0Eu/SEPxPrGKQmqRNF89OvXXYSqIiwDEYnkjLos3m/SqyCCx3x3elxJxADoUiCYlBKHYvNcJpu6wIDAQABAoGAXRqvXmykoxKuNXbT07KozPL6xBEMlNPdBYxVmgMbSTvmfuGsIEAjgVz7ys+BPxPK76wPCe31GEyCe/Ig22uVbp7fM4KjC3PgwGVFUAQpHU8xjlVB/IJzt1ZrTqtyN0gxqbeio7gUvwdedd3iOg9ZPoOi1dzKyX3RbQECQQDglAdWBerchua0sgqU4Ao9U+3f0qjya0jaWayANAbvc/h9tHptuAzL8KuLltnWYiNPfOJ/9DQVPYcX1uycgcDZAkEAzScbYIXd5p2cvvb7fDO5qxD/VDzbZwTeygZl4eVRsG7WmErxwgEb5LnwoiMCEi3NsD/zc5O/hnLbL/xZ8ArTYwJAIzdzUw0IgF5zLSDCQghD8swHgFLDhYhHW142i8Ac2k3gSK4ViMEU2KI79F7KeDDZgqx9xDziMfZ9CcL9RamfOQJAP5bxm3ejEoqkPWUdCqCV2nqISjoa39HYpNJa3ixQp9mTVt1UFoh1du5Tsk4bpDriWNFZyudvJGORoUwdLJSOdQJATEod3RUAdGsFJv6A3xSWgkV+Lt+X7yT3FF3WB+jB+YCrmDLdAkKbHzNLLbmprh3dggGVe8MKks6cHwCH7aDhIw==";

	//将RSA私钥转换成PKCS8格式
	public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANsw9XNYs2GWps4O" +
			"MKksMkYqZypkvS7EzlPvDcR6cjqxJFMFI26U67YbUBdKEzIpCofpGCotTQ8UyZN9" +
			"tiVPud/kuPW0n9RU7CeQapA0hk1zZI+uXhGt1ktUQBTT+MpMe8szBrS1qhKJoonK" +
			"q/vfw+08OkesFo6oCIHkStEdLrntAgMBAAECgYBgqtPlHf5mkJFaGMn/If2+Eh9T" +
			"hAAnKyavv6L7vuC3373cW0zIDSdzNdJ5ovKaUZ1SWUuN9lKgzxMjV/LHu8SGF1pr" +
			"slK2uxOsqIdgNplKiPYToRdD0LHiB2VVd26SmNHJ/cbDOI2Wj5lXivruTcQzA9uw" +
			"0yv3ZFGnr16kOpAtJQJBAPli12bq5SykCsGFijzVwlL7i4KNf7vQn/It6DM+H1II" +
			"IQIilo9h3oVdpntSzKJAm4PEC29IYvwayl40QZZCq5sCQQDhAR2CN7FYP67MwDlQ" +
			"Ih5JdT4s8MaX0Gna3w09bj0ebSgMBUwCZzYWbV98Nv5A9DcWHuEiKipbsxUFMfEd" +
			"Ll0XAkEAoNN1RhHFqXxA03xjIchYgVtnJNJLxbtM6slgLWuqlyRW5SGZJu5eqnMy" +
			"oeVLwncX02niVenArAQ67XWVtmlYcQJAYwPCoJMxj8w2eBP/JKxe96SIf+5U9mnY" +
			"q2CMywPAEaune+K42DXjL2tiIZ9xs9PEig98szQq/7+G+IpJuLW2cQJAWbKtHArv" +
			"sJBc6SfjVp216Dj1KPTXQ9+CMhrDGFA8WQ08XRiinW1STM/eHtCSa0Ran4TEL57a" +
			"Zvan+b6WSGNppQ==";

	//支付宝默认公钥 ，请勿修改
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";


}
