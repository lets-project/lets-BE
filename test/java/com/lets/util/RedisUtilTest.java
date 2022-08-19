package com.lets.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisUtilTest {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtil redisUtil;


    @AfterEach
    void tearDown() throws Exception{
        redisTemplate.delete("key1");
    }

    @Test
    void getData(){
        //given
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("key1", "value1");

        //when
        String value = redisUtil.getData("key1");

        //then
        assertThat(value).isEqualTo("value1");
    }

    @Test
    void setData(){
        //given


        //when
        redisUtil.setData("key1", "value1");

        //then
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = (String)valueOperations.get("key1");
        assertThat(value).isEqualTo("value1");
    }

    @Test
    void deleteData(){
        //given
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("key1", "value1");


        //when
        redisUtil.deleteData("key1");

        //then
        String value = (String)valueOperations.get("key1");
        assertThat(value).isBlank();
    }
}
