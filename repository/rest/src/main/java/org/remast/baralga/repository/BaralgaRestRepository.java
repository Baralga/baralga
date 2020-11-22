package org.remast.baralga.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.*;

public class BaralgaRestRepository implements BaralgaRepository {

    private String baseUrl;
    private ObjectMapper objectMapper;
    private OkHttpClient client;
    private DateTimeFormatter isoDateTimeFormatter;

    public BaralgaRestRepository(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void close() {
        client = null;
    }

    @Override
    public void initialize() {
        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
        isoDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    private Request addBasicAuthHeaders(Request request) {
        final String login = "admin";
        final String password = "admin22";
        String credential = Credentials.basic(login, password);
        return request.newBuilder().header("Authorization", credential).build();
    }

    @Override
    public void gatherStatistics() {
        // do nothing
    }

    @Override
    public void clearData() {
        // not allowed by client
    }

    @Override
    public List<ActivityVO> getActivities() {
        return getActivities(null);
    }

    @Override
    public void addActivity(ActivityVO activity) {
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("activities")
                .build();

        ObjectNode activityJson = objectMapper.createObjectNode();
        activityJson.put("start", isoDateTimeFormatter.print(activity.getStart()));
        activityJson.put("end", isoDateTimeFormatter.print(activity.getEnd()));
        activityJson.put("description", activity.getDescription());
        activityJson.put("projectRef", activity.getProject().getId());

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(activityJson)))
                    .build();

            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeActivity(ActivityVO activity) {
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("activities")
                .addPathSegment("id").addPathSegment(activity.getId())
                .build();

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();

            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addActivities(Collection<ActivityVO> activities) {
        activities.stream().forEach(this::addActivity);
    }

    @Override
    public void removeActivities(Collection<ActivityVO> activities) {
        activities.stream().forEach(this::removeActivity);
    }

    @Override
    public void updateActivity(ActivityVO activity) {
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("activities")
                .addPathSegment(activity.getId())
                .build();

        ObjectNode activityJson = objectMapper.createObjectNode();
        activityJson.put("id", activity.getId());
        activityJson.put("start", isoDateTimeFormatter.print(activity.getStart()));
        activityJson.put("end", isoDateTimeFormatter.print(activity.getEnd()));
        activityJson.put("description", activity.getDescription());
        activityJson.put("projectRef", activity.getProject().getId());

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .put(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(activityJson)))
                    .build();

            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ActivityVO> getActivities(FilterVO filter) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("activities");

        if (filter != null && filter.getTimeInterval() != null) {
            urlBuilder.addQueryParameter("start", isoDateTimeFormatter.print(filter.getTimeInterval().getStart()));
            urlBuilder.addQueryParameter("end", isoDateTimeFormatter.print(filter.getTimeInterval().getEnd()));
        }

        HttpUrl url = urlBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }

            try (ResponseBody responseBody = response.body()) {
                String bb = responseBody.string();
                JsonNode jsonActivities = objectMapper.readTree(bb);

                final List<ProjectVO> projects = new ArrayList<>();
                final Map<String, ProjectVO> projectsById = new HashMap<>();
                for (JsonNode jsonProject : jsonActivities.get("projectRefs")) {
                    ProjectVO project = readProject(jsonProject);
                    projectsById.put(project.getId(), project);
                    projects.add(project);
                }

                final List<ActivityVO> activities = new ArrayList<>();
                for (JsonNode jsonActivity : jsonActivities.get("data")) {
                    ActivityVO activity = new ActivityVO(
                            jsonActivity.get("id").asText(),
                            isoDateTimeFormatter.parseDateTime(jsonActivity.get("start").asText()),
                            isoDateTimeFormatter.parseDateTime(jsonActivity.get("end").asText()),
                            jsonActivity.get("description").isNull() ? null : jsonActivity.get("description").asText(),
                            projectsById.get(jsonActivity.get("projectRef").asText())
                    );
                    activities.add(activity);
                }

                return activities;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProject(ProjectVO project) {
        HttpUrl url = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("projects")
                .build();

        ObjectNode projectJson = objectMapper.createObjectNode();
        projectJson.put("id", project.getId());
        projectJson.put("title", project.getTitle());
        projectJson.put("description", project.getDescription());

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), projectJson.asText()))
                .build();

        try {
            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(ProjectVO project) {
        // not allowed by client
    }

    @Override
    public List<ProjectVO> getActiveProjects() {
        return getProjects(true);
    }

    @Override
    public List<ProjectVO> getAllProjects() {
        return getProjects(null);
    }

    private List<ProjectVO> getProjects(Boolean active) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("projects");

        if (active != null) {
            urlBuilder.addQueryParameter("active", active ? "true" : "false");
        }

        HttpUrl url = urlBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }

            try (ResponseBody responseBody = response.body()) {
                JsonNode jsonProjects = objectMapper.readTree(responseBody.string());

                final List<ProjectVO> projects = new ArrayList<>();
                for (JsonNode jsonProject : jsonProjects) {
                    ProjectVO project = readProject(jsonProject);
                    projects.add(project);
                }

                return projects;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ProjectVO> findProjectById(String projectId) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api").addPathSegment("projects")
                .addPathSegment(projectId);

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try {
            Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (response.code() == 404) {
                return Optional.empty();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }

            try (ResponseBody responseBody = response.body()) {
                JsonNode jsonProject = objectMapper.readTree(responseBody.string());
                ProjectVO project = readProject(jsonProject);
                return Optional.ofNullable(project);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addProjects(Collection<ProjectVO> projects) {
        projects.stream().forEach(this::addProject);
    }

    @Override
    public void updateProject(ProjectVO project) {
        // not yet implemented
    }

    private ProjectVO readProject(JsonNode jsonProject) {
        return new ProjectVO(
                jsonProject.get("id").asText(),
                jsonProject.get("title").asText(),
                jsonProject.get("description").asText()
        );
    }
}
