package com.insight.board.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
// application.properties를 사용할 수 있도록 설정 파일의 위치를 지정해 준다.
// @PropertySource() 으로 다른 설정 파일도 사용할 수 있다.
@PropertySource("classpath:/application.properties")
public class DatabaseConfiguration {

    // MyBatis 설정
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // 1.스프링-마이바티스에서는 SqlSessionFacotry를 생성하기 위해서 SqlSessionFactoryBean을 사용
        //      만약 스프링이 아닌 단독으로 사용하게 될 경우 SqlSessionFacotyBean을 사용
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 2.앞에서 만든 데이터 소스를 설정
        sqlSessionFactoryBean.setDataSource(dataSource);
        // 3.마이바티스 매퍼(Mapper) 파일의 위치를 설정.
        //   매퍼는 애플리케이션에서 사용할 SQL을 담고 있는 XML 파일을 의미
        sqlSessionFactoryBean.setMapperLocations(applicationContext.getResource("classpath:/mapper/**/sql-*.xml"));
        return sqlSessionFactoryBean.getObject();
    }
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFacory) {
        return new SqlSessionTemplate(sqlSessionFacory);
    }

    // application.properties에 설정했던 DB 관련 정보를 사용하도록 지정
    // @ConfigurationProperties 어노테이션에 prefix가 spring.datasource.hikari로 설정되어 있기 때문에
    // spring.datasource.hikari로 시작하는 설정을 이용해서 히카리CP의 설정 파일을 만든다.
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    // 앞에서 만든 히카리CP의 설정 파일을 이용해서 DB와 연결하는 데이터 소스를 생성한다.
    // 여기서는 데이터 소스사 정상적으로 생성되었는지 확인하기 위해서 데이터 소스를 출력했다.
    @Bean
    public DataSource dataSource() throws Exception {
        DataSource dataSource = new HikariDataSource(hikariConfig());
        System.out.println(dataSource.toString());
        return dataSource;
    }
}
