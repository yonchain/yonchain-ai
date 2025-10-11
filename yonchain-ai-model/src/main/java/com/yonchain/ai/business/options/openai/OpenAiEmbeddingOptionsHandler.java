/*
package com.yonchain.ai.model.options.openai;

import com.yonchain.ai.model.options.ModelOptionsHandler;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;

import java.util.Map;

*/
/**
 * OpenAI嵌入模型选项处理器
 *//*

public class OpenAiEmbeddingOptionsHandler implements ModelOptionsHandler<OpenAiEmbeddingOptions> {


    @Override
    public OpenAiEmbeddingOptions buildOptions(Map<String, Object> config) {
        var builder = OpenAiEmbeddingOptions.builder();

        // 设置模型 (必需)
        if (config.containsKey("model")) {
            builder.model((String) config.get("model"));
        }

        // 设置用户ID
        if (config.containsKey("user")) {
            builder.user((String) config.get("user"));
        }

        // 设置编码格式 (float, base64)
        if (config.containsKey("encodingFormat")) {
            builder.encodingFormat((String) config.get("encodingFormat"));
        }

        // 设置维度（仅适用于text-embedding-3-small和text-embedding-3-large）
        if (config.containsKey("dimensions")) {
            Object dimensions = config.get("dimensions");
            if (dimensions instanceof Number) {
                builder.dimensions(((Number) dimensions).intValue());
            }
        }

        return builder.build();
    }

    @Override
    public boolean validateConfig(Map<String, Object> config) {
        // 验证必需的模型参数
        if (!config.containsKey("model")) {
            return false;
        }

        // 验证编码格式
        if (config.containsKey("encodingFormat")) {
            String format = (String) config.get("encodingFormat");
            if (!format.equals("float") && !format.equals("base64")) {
                return false;
            }
        }

        // 验证维度设置
        if (config.containsKey("dimensions")) {
            Object dimensions = config.get("dimensions");
            if (dimensions instanceof Number) {
                int dimValue = ((Number) dimensions).intValue();
                String model = (String) config.get("model");
                
                // 根据模型验证维度范围
                if ("text-embedding-3-small".equals(model)) {
                    if (dimValue < 1 || dimValue > 1536) {
                        return false;
                    }
                } else if ("text-embedding-3-large".equals(model)) {
                    if (dimValue < 1 || dimValue > 3072) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
*/
