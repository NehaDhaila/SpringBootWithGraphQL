package com.neha.SpringBootWithGraphQl.controller;

import com.neha.SpringBootWithGraphQl.entity.PersonEntity;
import com.neha.SpringBootWithGraphQl.repository.PersonRepository;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @Value("classpath:schema.graphqls")
    private Resource schemaResource;
    private GraphQL graphQL;

    @PostConstruct
    public void loadSchema() throws IOException {
        File schemaFile;
        schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry registry= new SchemaParser().parse(schemaFile); //to get the schema file
        RuntimeWiring wiring=buidWiring();
        GraphQLSchema schema=new SchemaGenerator().makeExecutableSchema(registry,wiring);
        graphQL=GraphQL.newGraphQL(schema).build();
    }

    private RuntimeWiring buidWiring(){  //(DataFatcher)>> ll fatch data from DB, and ll do runtime wiring with schema methods(getAllPerson,findPerson) using datafatcher
        DataFetcher<List<PersonEntity>> fatcher1=data->{
            return (List<PersonEntity>) personRepository.findAll();
        };

        DataFetcher<PersonEntity> fatcher2=data->{
            return personRepository.findByEmail(data.getArgument("email"));
        };

        return RuntimeWiring.newRuntimeWiring().type("Query",typeWriting ->
            typeWriting.dataFetcher("getAllPerson",fatcher1).dataFetcher("findPerson",fatcher2))
                .build();
    }

    @PostMapping("/getAll")
    public ResponseEntity<Object> getAll(@RequestBody String query){
        ExecutionResult result=graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
    @PostMapping("/getPersonByEmail")
    public ResponseEntity<Object> getPersonByEmail(@RequestBody String query){
        ExecutionResult result=graphQL.execute(query);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
    @PostMapping("/addPerson")
    public String addPerson(@RequestBody List<PersonEntity> persons){
        System.out.println(">> "+persons);
        //personRepository.save(persons);
        personRepository.saveAll(persons);
        return ""+persons.size();
    }
    @GetMapping("/findAllPersonss")
    public List<PersonEntity> findAllPersons() {
        return (List<PersonEntity>)personRepository.findAll();
    }
    }


//commenting for checking github commit