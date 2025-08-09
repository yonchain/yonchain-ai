package com.yonchain.ai.dify;

import com.yonchain.ai.api.dify.DifyAppService;
import com.yonchain.ai.api.exception.YonchainException;
import com.yonchain.ai.dify.service.DifyAppServiceImpl;

/**
 * dify 配置类
 *
 * @author Cgy
 */
public class DifyConfiguration {

    protected DifyAppService difyAppService;


    protected DifyConfiguration(Builder builder) {
        this.difyAppService = builder.difyAppService;

        // 初始化默认组件
        init();
    }

    private void init() {
         if (difyAppService == null) {
             difyAppService = new DifyAppServiceImpl();
         }
    }


    /**
     * 创建新的安全配置构建器
     *
     * @return 安全配置构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 安全配置构建器
     * 用于构建SecurityConfiguration实例
     */
    public static class Builder {

        public DifyAppService difyAppService;

        public Builder difyAppService(DifyAppService difyAppService) {
            this.difyAppService = difyAppService;
            return this;
        }

        /**
         * 构建安全配置实例
         *
         * @return 安全配置实例
         */
        public DifyConfiguration build() {
            // 验证配置属性
            validate();

            // 返回配置实例
            return new DifyConfiguration(this);
        }

        private void validate() {
            /*// 验证必需的组件
            if (difyAppService == null) {
                throw new YonchainException("dify应用服务未设置");
            }*/
        }
    }

}
