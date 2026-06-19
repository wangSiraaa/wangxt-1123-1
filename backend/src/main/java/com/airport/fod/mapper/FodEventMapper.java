package com.airport.fod.mapper;

import com.airport.fod.entity.FodEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FodEventMapper extends BaseMapper<FodEvent> {

    @Select("SELECT * FROM fod_event WHERE dr = 0 ORDER BY is_top DESC, create_time DESC")
    List<FodEvent> selectOrderedList();

    @Select("SELECT COUNT(*) FROM fod_event WHERE dr = 0 AND status = #{status}")
    Integer countByStatus(@Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM fod_event WHERE dr = 0 AND is_top = 1")
    Integer countTopEvents();
}
