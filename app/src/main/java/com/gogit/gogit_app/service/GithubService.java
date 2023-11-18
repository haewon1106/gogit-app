package com.gogit.gogit_app.service;

import com.gogit.gogit_app.model.github.commit.RepoCommit;
import com.gogit.gogit_app.model.github.issue.Issue;
import com.gogit.gogit_app.model.github.org.Organization;
import com.gogit.gogit_app.model.github.repo.SearchedRepo;
import com.gogit.gogit_app.model.github.user.SearchedUser;
import com.gogit.gogit_app.request.AddIssueRequest;
import com.gogit.gogit_app.request.AddRepositoryRequest;
import com.gogit.gogit_app.model.github.user.GithubUser;
import com.gogit.gogit_app.model.github.repo.Repository;

import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubService {
    @GET("users/{login}")
    Call<GithubUser> getUser(
            @Header("Authorization") String auth,
            @Path("login") String username
    );

    @GET("users/{login}/followers")
    Call<List<GithubUser>> getFollowers(
            @Header("Authorization") String auth,
            @Path("login") String username
    );

    @GET("users/{login}/repos?sort=updated")
    Call<List<Repository>> getRepos(
            @Header("Authorization") String auth,
            @Path("login") String username
    );

    @POST("user/repos")
    Call<Map<String, Object>> createRepo(
            @Header("Authorization") String auth,
            @Body AddRepositoryRequest addRepositoryRequest
    );

    @GET("/search/users")
    Call<SearchedUser> getUserSearchResult(
            @Header("Authorization") String auth,
            @Query("q") String keyword,
            @Query("per_page") Integer perPage
    );

    @GET("/search/repositories")
    Call<SearchedRepo> getRepoSearchResult(
            @Header("Authorization") String auth,
            @Query("q") String keyword,
            @Query("per_page") Integer perPage
    );

    @GET("/issues")
    Call<List<Issue>> getUesrsIssues(
            @Header("Authorization") String auth
    );

    @GET("/repos/{owner}/{repo}/commits")
    Call<List<RepoCommit>> getReposCommits(
            @Header("Authorization") String auth,
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    @POST("/repos/{login}/{repo}/issues")
    Call<Issue> createIssue(
            @Header("Authorization") String auth,
            @Path("login") String login,
            @Path("repo") String repo,
            @Body AddIssueRequest addIssueRequest
    );

    @DELETE("/repos/{login}/{repo}")
    Call<Object> deleteRepo(
            @Header("Authorization") String auth,
            @Path("login") String login,
            @Path("repo") String repo
    );

    @PATCH("/repos/{login}/{repo}/issues/{number}")
    Call<Map<String, Object>> closeIssue(
            @Header("Authorization") String auth,
            @Path("login") String login,
            @Path("repo") String repo,
            @Path("number") Long number,
            @Body Map<String, String> state
    );

    @GET("/users/{login}/orgs")
    Call<List<Organization>> getUsersOrgs(
            @Header("Authorization") String auth,
            @Path("login") String login
    );
}
