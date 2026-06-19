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

    @Select("SELECT * FROM fod_event WHERE dr = 0 AND runway_id = #{runwayId} AND location = #{location} " +
            "AND status NOT IN (8, 9) AND merged_parent_id IS NULL " +
            "AND DATE(report_time) = CURDATE() ORDER BY create_time DESC LIMIT 1")
    FodEvent findSameLocationToday(@Param("runwayId") Long runwayId, @Param("location") String location);
}
