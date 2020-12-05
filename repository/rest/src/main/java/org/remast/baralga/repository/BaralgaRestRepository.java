package org.remast.baralga.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BaralgaRestRepository implements BaralgaRepository {


    private String baseUrl;
    private String user;
    private String password;
    private ObjectMapper objectMapper;
    private OkHttpClient client;
    private DateTimeFormatter dateFormat;
    private DateTimeFormatter isoDateTimeFormatter;

    public BaralgaRestRepository(final String baseUrl, final String user, final String password) {
        this.baseUrl = baseUrl;
        this.user = user;
        this.password = password;
    }

    @Override
    public void close() {
        client = null;
    }

    @Override
    public void initialize() {
        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
        dateFormat =  DateTimeFormat.forPattern("yyyy-MM-dd");
        isoDateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
    }

    private Request addBasicAuthHeaders(Request request) {
        final String credential = Credentials.basic(user, password);
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
    public ActivityVO addActivity(ActivityVO activity) {
        final HttpUrl url = activitiesUrl().build();

        final ObjectNode activityJson = createActivity(activity);

        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), writeValueAsJsonString(activityJson)))
                .build();

        final Response response = execute(request);
        try (ResponseBody responseBody = response.body()) {
            JsonNode jsonActivity = readTreeFromJsonString(responseBody.string());
            ActivityVO activityUpdated = readActivity(jsonActivity);
            activityUpdated.setProject(activity.getProject());
            return activityUpdated;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeActivity(ActivityVO activity) {
        final HttpUrl url = activitiesUrl()
                .addPathSegment(activity.getId())
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        execute(request);
    }

    @Override
    public Collection<ActivityVO> addActivities(Collection<ActivityVO> activities) {
        return activities.stream().map(this::addActivity).collect(Collectors.toList());
    }

    @Override
    public void removeActivities(Collection<ActivityVO> activities) {
        activities.stream().forEach(this::removeActivity);
    }

    @Override
    public void updateActivity(ActivityVO activity) {
        final HttpUrl url = activitiesUrl()
                .addPathSegment(activity.getId())
                .build();

        final ObjectNode activityJson = createActivity(activity);

        final Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), writeValueAsJsonString(activityJson)))
                .build();

        execute(request);
    }

    @Override
    public List<ActivityVO> getActivities(FilterVO filter) {
        final HttpUrl.Builder urlBuilder = activitiesUrl();

        if (filter != null && filter.getTimeInterval() != null) {
            urlBuilder.addQueryParameter("start", dateFormat.print(filter.getTimeInterval().getStart()));
            urlBuilder.addQueryParameter("end", dateFormat.print(filter.getTimeInterval().getEnd()));
        }

        final Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        final Response response = execute(request);

        try (ResponseBody responseBody = response.body()) {
            final JsonNode jsonActivities = readTreeFromJsonString(responseBody.string());

            final List<ProjectVO> projects = new ArrayList<>();
            final Map<String, ProjectVO> projectsById = new HashMap<>();
            for (JsonNode jsonProject : jsonActivities.get("projectRefs")) {
                ProjectVO project = readProject(jsonProject);
                projectsById.put(project.getId(), project);
                projects.add(project);
            }

            final List<ActivityVO> activities = new ArrayList<>();
            for (JsonNode jsonActivity : jsonActivities.get("data")) {
                ActivityVO activity = readActivity(jsonActivity);
                activity.setProject(projectsById.get(jsonActivity.get("projectRef").asText()));
                activities.add(activity);
            }

            return activities;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ProjectVO addProject(ProjectVO project) {
        final HttpUrl url = projectsUrl().build();

        final ObjectNode projectJson = createProject(project);

        final Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), writeValueAsJsonString(projectJson)))
                .build();

        final Response response = execute(request);

        try (ResponseBody responseBody = response.body()) {
            JsonNode jsonProject = objectMapper.readTree(responseBody.string());
            return readProject(jsonProject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(ProjectVO project) {
        final HttpUrl url = projectsUrl()
                .addPathSegment(project.getId())
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        execute(request);
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
        final HttpUrl.Builder urlBuilder = projectsUrl();

        if (active != null) {
            urlBuilder.addQueryParameter("active", active ? "true" : "false");
        }

        final Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        final Response response = execute(request);

        try (ResponseBody responseBody = response.body()) {
            final JsonNode jsonProjects = readTreeFromJsonString(responseBody.string());

            final List<ProjectVO> projects = new ArrayList<>();
            for (JsonNode jsonProject : jsonProjects) {
                ProjectVO project = readProject(jsonProject);
                projects.add(project);
            }

            return projects;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ProjectVO> findProjectById(String projectId) {
        final HttpUrl url = projectsUrl().addPathSegment(projectId).build();
        final Request request = new Request.Builder()
                .url(url)
                .build();

        final Response response = execute(request, 404);
        if (response.code() == 404) {
            return Optional.empty();
        }

        try (ResponseBody responseBody = response.body()) {
            JsonNode jsonProject = readTreeFromJsonString(responseBody.string());
            ProjectVO project = readProject(jsonProject);
            return Optional.ofNullable(project);
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
        final HttpUrl url = projectsUrl()
                .addPathSegment(project.getId())
                .build();

        final ObjectNode projectJson = createProject(project);

        final Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), writeValueAsJsonString(projectJson)))
                .build();

        execute(request);
    }

    private ObjectNode createActivity(ActivityVO activity) {
        final ObjectNode activityJson = objectMapper.createObjectNode();
        activityJson.put("id", activity.getId());
        activityJson.put("start", isoDateTimeFormatter.print(activity.getStart()));
        activityJson.put("end", isoDateTimeFormatter.print(activity.getEnd()));
        activityJson.put("description", activity.getDescription());
        activityJson.put("projectRef", activity.getProject().getId());
        return activityJson;
    }

    private ObjectNode createProject(ProjectVO project) {
        final ObjectNode projectJson = objectMapper.createObjectNode();
        projectJson.put("title", project.getTitle());
        projectJson.put("description", project.getDescription());
        projectJson.put("active", project.isActive() ? "true" : "false");
        return projectJson;
    }

    private ActivityVO readActivity(JsonNode jsonActivity) {
        return new ActivityVO(
                jsonActivity.get("id").asText(),
                isoDateTimeFormatter.parseDateTime(jsonActivity.get("start").asText()),
                isoDateTimeFormatter.parseDateTime(jsonActivity.get("end").asText()),
                jsonActivity.get("description").isNull() ? null : jsonActivity.get("description").asText(),
                null
        );
    }

    private ProjectVO readProject(JsonNode jsonProject) {
        return new ProjectVO(
                jsonProject.get("id").asText(),
                jsonProject.get("title").asText(),
                jsonProject.get("description").asText()
        );
    }

    private String writeValueAsJsonString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonNode readTreeFromJsonString(String jsonString) {
        try {
            return objectMapper.readTree(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response execute(Request request, int... acceptedReturnCodes) {
        try {
            final Response response = client.newCall(addBasicAuthHeaders(request)).execute();
            if (acceptedReturnCodes != null && Arrays.stream(acceptedReturnCodes).anyMatch(i -> i == response.code())) {
                return response;
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException(response.toString());
            }

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpUrl.Builder activitiesUrl() {
        return baseUrl().addPathSegment("activities");
    }

    private HttpUrl.Builder projectsUrl() {
        return baseUrl().addPathSegment("projects");
    }

    private HttpUrl.Builder baseUrl() {
        return HttpUrl.parse(baseUrl).newBuilder()
                .addPathSegment("api");
    }
}
