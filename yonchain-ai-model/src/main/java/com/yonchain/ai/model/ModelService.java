package com.yonchain.ai.model;


import org.springframework.ai.model.Model;

public interface ModelService<M extends Model> {


    M getModel(String model);
}
