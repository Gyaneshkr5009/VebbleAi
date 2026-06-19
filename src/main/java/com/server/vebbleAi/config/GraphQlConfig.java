package com.server.vebbleAi.config;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQlConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType matrixScalar = GraphQLScalarType.newScalar()
                .name("Matrix")
                .description("A custom scalar to handle 2D arrays")
                .coercing(new Coercing<Object, Object>() {
                    @Override
                    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
                        // Directly hands over your Character[][] array to the JSON serializer
                        return dataFetcherResult;
                    }

                    @Override
                    public Object parseValue(Object input) throws CoercingParseValueException {
                        return input;
                    }

                    @Override
                    public Object parseLiteral(Object input) throws CoercingParseLiteralException {
                        return input;
                    }
                })
                .build();

        return wiringBuilder -> wiringBuilder.scalar(matrixScalar);
    }
}
