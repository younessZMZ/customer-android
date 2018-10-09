package com.kustomer.kustomersdk.Models;

import com.kustomer.kustomersdk.Enums.KUSFormQuestionProperty;
import com.kustomer.kustomersdk.Enums.KUSFormQuestionType;
import com.kustomer.kustomersdk.Helpers.KUSInvalidJsonException;
import com.kustomer.kustomersdk.Utils.JsonHelper;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Junaid on 1/20/2018.
 */

public class KUSFormQuestion extends KUSModel {

    //region Properties
    private String name;
    private String prompt;
    private List<String> values;
    private KUSFormQuestionType type;
    private KUSFormQuestionProperty property;
    private Boolean skipIfSatisfied;
    //endregion

    //region LifeCycle
    public KUSFormQuestion(JSONObject json) throws KUSInvalidJsonException {
        super(json);

        name = JsonHelper.stringFromKeyPath(json, "name");
        prompt = JsonHelper.stringFromKeyPath(json, "prompt");
        skipIfSatisfied = JsonHelper.boolFromKeyPath(json, "skipIfSatisfied");
        type = KUSFormQuestionTypeFromString(JsonHelper.stringFromKeyPath(json, "type"));
        property = KUSFormQuestionPropertyFromString(JsonHelper.stringFromKeyPath(json, "property"));
        values = JsonHelper.arrayListFromKeyPath(json, "values");
    }
    //endregion

    //region Class Methods
    public String modelType() {
        return null;
    }

    public boolean enforcesModelType() {
        return false;
    }

    public static boolean KUSFormQuestionRequiresResponse(KUSFormQuestion question) {
        if (question == null || question.type == null)
            return false;

        return question.type == KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_PROPERTY ||
                question.type == KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_RESPONSE;
    }

    private static KUSFormQuestionType KUSFormQuestionTypeFromString(String string) {
        switch (string) {
            case "message":
                return KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_MESSAGE;
            case "property":
                return KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_PROPERTY;
            case "response":
                return KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_RESPONSE;
        }

        return KUSFormQuestionType.KUS_FORM_QUESTION_TYPE_UNKNOWN;
    }

    private static KUSFormQuestionProperty KUSFormQuestionPropertyFromString(String string) {
        if (string == null)
            return null;

        switch (string) {
            case "customer_name":
                return KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CUSTOMER_NAME;
            case "customer_email":
                return KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CUSTOMER_EMAIL;
            case "conversation_team":
                return KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CONVERSATION_TEAM;
            case "customer_phone":
                return KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CUSTOMER_PHONE;
            case "followup_channel":
                return KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_CUSTOMER_FOLLOW_UP_CHANNEL;
        }

        return KUSFormQuestionProperty.KUS_FORM_QUESTION_PROPERTY_UNKNOWN;
    }
    //endregion

    //region Accessors

    public KUSFormQuestionProperty getProperty() {
        return property;
    }

    public KUSFormQuestionType getType() {
        return type;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public Boolean getSkipIfSatisfied() {
        return skipIfSatisfied != null ? skipIfSatisfied : false;
    }

    //endregion
}
