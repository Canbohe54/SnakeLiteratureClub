package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Grade;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GradeDao {
    /**
     * 插入专家评分
     * @param grade
     */
    @Insert("insert into grades(expert_id,article_id,grade_expr,grade_struct,grade_theme,grade_all,advice) "+
    "values (#{expert_id},#{article_id}),#{grade_expr},#{grade_struct},#{grade_theme},#{grade_all},#{advice}")
    public void insertGrade(@Param("grade") Grade grade);

    /**
     * 删除专家id和稿件id删除评分信息
     * @param expert_id
     * @param article_id
     */
    @Delete("delete from grades where expert_id = #{expert_id} and article = #{article_id}")
    public void deleteGradeByExpertIdAndArticleId(@Param("expert_id") String expert_id,@Param("article_id") String article_id);

    /**
     * 更新评分信息
     * @param grade 封装有评分信息的Grade对象
     */
    @Update("update grades set grade_expr = #{grade_expr},grade_struct = #{grade_struct},grade_theme = #{grade_theme},grade_all = #{grade_all},advice = #{advice} "+
    "where article_id = #{article_id} and expert_id = #{expert_id}")
    public void updateGrade(@Param("grade") Grade grade);

    /**
     * 根据稿件id查询稿件评分
     * @param article_id
     * @return 该稿件id对应的Grade对象列表
     */
    @Select("select * from grades where article_id = #{article_id}")
    public List<Grade> getGradeByArticle_id(@Param("article_id") String article_id);

    /**
     * 根据专家id查询稿件评分
     * @param expert_id
     * @return 该稿件id对应的Grade对象列表
     */
    @Select("select * from grades where expert_id = #{expert_id}")
    public List<Grade> getGradeByExpert_id(@Param("expert_id") String expert_id);

    /**
     * 根据专家id和稿件id查询稿件评分
     * @param expert_id
     * @param article_id
     * @return 该稿件id和专家id对应的Grade对象
     */
    @Select("select * from grades where expert_id = #{expert_id} and article_id = #{article_id}")
    public Grade getGradeByExpertIdAndArticleId(@Param("expert_id") String expert_id,@Param("article_id") String article_id);

    /**
     * 根据稿件id查询总分的平均分
     * @param article_id 稿件id
     * @return 平均分
     */
    @Select("select avg(grade_all) from grades where article_id = #{article_id} group by article_id ")
    public int getAvgGradeByArticleId(@Param("article_id") String article_id);

    /**
     * 根据稿件id查询评分的专家人数
     * @param article_id 稿件id
     * @return 评分专家人数
     */
    @Select("select count(expert_id) from grades where article_id = #{article_id} group by article_id ")
    public int getCountByArticleId(@Param("article_id") String article_id);

    /**
     * 根据专家id查询稿件评分,按照总分由高到低排序
     * @param expert_id 专家id
     * @return
     */
    @Select("select * from grades where expert_id = #{expert_id} order by grade_all")
    public List<Grade> getSortByGradeAll(@Param("expert_id") String expert_id);
}
