package com.yonchain.ai.autoconfigure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT安全配置属性类
 *
 * @author Cgy
 * @since 1.0.0
 */
@ConfigurationProperties("dify4j.security.oauth2.jwt")
public class OAuth2JwtProperties {

    //默认私钥
    private static final String DEFAULT_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCbE9KvE2zcOnDNSm1xTYewJei+nxzbN6PiVIg8CP99S5C7Bs/Syi3w2DTMlb/jCKlp4u+DNlSbIOzwb6rjqLZOPDVyaX1jdR0nX7tvmMpb+JQRbSGl6QbOpSUodNIb3D6jfrQ4imqK/jPCNPTQCDisApQ810/bfP8dUduHFZw6T5DoeHbVy7DAgG/fILDriFKdA0Ut5hETl03jtGwxNfaCXZjtA8dEYwT2rt7EnMcS6GZSFRDBxIpRUr6Tm1s6PZi1+kNqgnvho7NdKctLA6OURsHHxyvUt6hZnzeS7I9pQleN/SZsaKLV/z8b0DDFom2m5T6TIbWNQ0NdCPcJ2qv3AgMBAAECggEALO2aTWS4VjmMMr3MZVYL6Kmi+48Hgi9liI3rBlGpC3jYDodTsIMoui05Zsg/1pvM0UORFpen/WWVn7cKcHiRkKr65RHQ5P6qGp0upY9DmU+0PjQ62mFfuoC3+jeWI8I0V5O+zN61LV2mSy6iep7XJGZ+4f2TQki9dJYhnspQ/8dX6P9q4dXOtCPu+Emq1+CGNNUHkhM7lEMAgK4S5TO7KT48QcB+TT7ezt1YtyIjQeCzh6ElvuPPwDkFTCOYtP/1SVlOz0tJQaytxA6O4kbiE1o2YupbWEPFnakCJO63YZ8Jhvgi5YdhCucbbOm0KfuEqIkJ5YoWjUMhn5+s8nE3AQKBgQDQMsrpmhaW5nnNjC9/+DxkzotupJT77Hdxenqc5iqKmI0obiw6RZ37wxy9g9O0uqxaZzgPrXtmF800J18KXpaKh4K2AZb5Fb07itmZBUonntHYFLGuljnm5ftn+M9hT79r2414ojQuKnDiWzKuC++t6wVj+fcUrVtWY1kxXWfPgQKBgQC+rsG8uujM+Qqfn9qC3X1iFAzb9IIttkc1EMG1r3T1+oqJjAEy4QK0v4ICjzshve/LkpFtbWno6bS13NG/vzVIYhJSED3X9/MmR8yJXFapyLdtd18hHthjr4ytSe7ZO9HccqBMz9LU7y8yDUAZmnZuZ30uHfyjEU/FyKihy0S3dwKBgCkyY8RloSKq6F8r5hpEXG72D9EM8A4JdHkX6zswsCpMlWHyqv282M4AhadadoHEjmY1FnrDsS86dnhjcgLzRIaU2eB54A3Zp2Ao0XXd/hZo7sz7C02onDx8NYsVSLPQu+fzwcKlFf4sl7myogyegAbCAocN5z5IL1Kgxf1mpiwBAoGAbLuEWFWQyMk+aJWj6bnZdKMQTiDYVq65XiYm/66EmHt8okCGsneirmUCAumgA3amlcQHT4nzudACry2zIY8sGBFsdwUx7sRCD9MjbDSOapS9HBvU0DMhm36QToR5QcCdxAJNLF/Zznhxxi3OHot4+y3iMPkChg4qGhN5LYGWtrMCgYAHCmuXjVyaYTkuspOvbRTpQKjEBzRl6SmDJkzwOGYL91hbXSZlz9l3rdo4+XmdrWxjU2a/SHEk6tMM9EAiJcqNmMyJl2vIJWSONuFSXA7Zg8z5QSEvEhezUcaBbrVsVakdtt28yyZXhhpqpb12gioOcMBpxD47bo9nwj4tdtGHag==";

    //默认公钥
    private static final String DEFAULT_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmxPSrxNs3DpwzUptcU2HsCXovp8c2zej4lSIPAj/fUuQuwbP0sot8Ng0zJW/4wipaeLvgzZUmyDs8G+q46i2Tjw1cml9Y3UdJ1+7b5jKW/iUEW0hpekGzqUlKHTSG9w+o360OIpqiv4zwjT00Ag4rAKUPNdP23z/HVHbhxWcOk+Q6Hh21cuwwIBv3yCw64hSnQNFLeYRE5dN47RsMTX2gl2Y7QPHRGME9q7exJzHEuhmUhUQwcSKUVK+k5tbOj2YtfpDaoJ74aOzXSnLSwOjlEbBx8cr1LeoWZ83kuyPaUJXjf0mbGii1f8/G9AwxaJtpuU+kyG1jUNDXQj3Cdqr9wIDAQAB";

    //默认密钥ID
    private static final String DEFAULT_KID = "8e5fe8b4-eb6c-4e36-9602-883b88888888";

    /**
     * RSA私钥，Base64编码
     * <p>
     * 用于签名JWT令牌。如果未提供，将自动生成一个随机密钥对。
     * </p>
     */
    private String privateKey = DEFAULT_PRIVATE_KEY;

    /**
     * RSA公钥，Base64编码
     * <p>
     * 用于验证JWT令牌的签名。如果未提供，将自动生成一个随机密钥对。
     * </p>
     */
    private String publicKey = DEFAULT_PUBLIC_KEY;

    /**
     * 密钥ID
     * <p>
     * 用于标识JWT头部中使用的密钥。如果未提供，将自动生成一个随机ID。
     * </p>
     */
    private String kid = DEFAULT_KID;

    /**
     * 获取RSA私钥
     *
     * @return Base64编码的RSA私钥
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置RSA私钥
     *
     * @param privateKey Base64编码的RSA私钥
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * 获取RSA公钥
     *
     * @return Base64编码的RSA公钥
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * 设置RSA公钥
     *
     * @param publicKey Base64编码的RSA公钥
     */
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * 获取密钥ID
     *
     * @return 密钥ID
     */
    public String getKid() {
        return kid;
    }

    /**
     * 设置密钥ID
     *
     * @param kid 密钥ID
     */
    public void setKid(String kid) {
        this.kid = kid;
    }
}
