/*
package com.yonchain.ai.model.options.openai;


import com.yonchain.ai.model.options.ModelOptionsHandler;
import org.springframework.ai.openai.OpenAiImageOptions;

import java.util.Map;

*/
/**
 * OpenAI图像模型选项处理器
 *//*

public class OpenAiImageOptionsHandler implements ModelOptionsHandler<OpenAiImageOptions> {


    @Override
    public OpenAiImageOptions buildOptions(Map<String, Object> config) {
        var builder = OpenAiImageOptions.builder();

        // 设置模型 (必需)
        if (config.containsKey("model")) {
            builder.model((String) config.get("model"));
        }

     */
/*   // 设置图像尺寸
        if (config.containsKey("size")) {
            builder.size((String) config.get("size"));
        }*//*


        // 设置图像质量 (standard, hd)
        if (config.containsKey("quality")) {
            builder.quality((String) config.get("quality"));
        }

        // 设置生成数量 (1-10)
        if (config.containsKey("n")) {
            Object n = config.get("n");
            if (n instanceof Number) {
                builder.N(((Number) n).intValue());
            }
        }

        // 设置响应格式 (url, b64_json)
        if (config.containsKey("responseFormat")) {
            builder.responseFormat((String) config.get("responseFormat"));
        }

        // 设置风格 (vivid, natural)
        if (config.containsKey("style")) {
            builder.style((String) config.get("style"));
        }

        // 设置用户ID
        if (config.containsKey("user")) {
            builder.user((String) config.get("user"));
        }

        return builder.build();
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        // 验证必需的模型参数
        if (!config.containsKey("model")) {
            return false;
        }

        // 验证图像尺寸
        if (config.containsKey("size")) {
            String size = (String) config.get("size");
            if (!isValidSize(size)) {
                return false;
            }
        }

        // 验证质量设置
        if (config.containsKey("quality")) {
            String quality = (String) config.get("quality");
            if (!quality.equals("standard") && !quality.equals("hd")) {
                return false;
            }
        }

        // 验证生成数量
        if (config.containsKey("n")) {
            Object n = config.get("n");
            if (n instanceof Number) {
                int nValue = ((Number) n).intValue();
                if (nValue < 1 || nValue > 10) {
                    return false;
                }
            }
        }

        // 验证风格
        if (config.containsKey("style")) {
            String style = (String) config.get("style");
            if (!style.equals("vivid") && !style.equals("natural")) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidSize(String size) {
        // DALL-E 3 支持的尺寸
        if (size.equals("1024x1024") || size.equals("1792x1024") || size.equals("1024x1792")) {
            return true;
        }
        // DALL-E 2 支持的尺寸
        if (size.equals("256x256") || size.equals("512x512")) {
            return true;
        }
        return false;
    }
}
*/
