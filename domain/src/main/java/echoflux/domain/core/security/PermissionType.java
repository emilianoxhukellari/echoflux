package echoflux.domain.core.security;

import lombok.Getter;

@Getter
public enum PermissionType {

    ROOT(
            "37b2d190-2d75-4722-be0f-4301de4b608a",
            "Grants all permissions"
    ),
    ERROR_VISIBLE(
            "ec1e218b-6b3d-41f7-a8ee-0875a1f862fa",
            "Permits the user to view full system error messages"
    ),

    TRANSCRIPTIONS_VIEW(
            "48c51cbd-eeb2-492b-bb34-8027e4a78856",
            "Access to transcriptions page"
    ),
    TRANSCRIBE_VIEW(
            "b11d5385-4953-4584-9871-66162bb13abf",
            "Access to transcribe page"
    ),
    TRANSCRIPTION_WORDS_VIEW(
            "1e76f830-1ac4-4a20-9a64-6a9ca0968ce7",
            "Access to transcription words page"
    ),
    TRANSCRIPTION_VIEW(
            "bf24244e-97de-4fd6-9295-103511f8866e",
            "Access to individual transcription details page"
    ),
    TRANSCRIPTION_CREATE(
            "24083f00-6be1-4ad0-bead-86b0c135a7fa",
            "Access to create new transcriptions"
    ),
    TRANSCRIPTION_UPDATE(
            "d1e2f3e4-2f3b-4c7a-9f0c-2e3b5f6a7b8c",
            "Access to update existing transcriptions"
    ),

    SETTINGS_VIEW(
            "656b548a-14aa-444e-98f9-780e012a8615",
            "Access to settings page"
    ),
    SETTINGS_SYNCHRONIZE(
            "638233d2-7e52-4f8b-bc7f-4ad534aec8b5",
            "Access to synchronize settings with their code-base schema"
    ),
    SETTINGS_RESET(
            "b6084831-08a6-44e7-94b3-73a1fa85c8bd",
            "Access to reset settings to their default values"
    ),

    ACCESS_MANAGEMENT_APPLICATION_USERS_VIEW(
            "3362cd64-34a3-4390-99ee-8ad70a61e39f",
            "Access to application users page"
    ),
    ACCESS_MANAGEMENT_APPLICATION_USER_CREATE(
            "21391398-1649-42b1-996e-e397c769bf08",
            "Access to create new application users"
    ),
    ACCESS_MANAGEMENT_APPLICATION_USER_UPDATE(
            "ca8663c9-719a-4d17-8f2f-b8af29a474ed",
            "Access to update existing application users"
    ),

    ACCESS_MANAGEMENT_ROLES_VIEW(
            "4b904626-551f-4431-bf18-d1f1f29b4dcc",
            "Access to roles page"
    ),
    ACCESS_MANAGEMENT_ROLE_CREATE(
            "f4403434-d643-46de-9bf0-53bca5fff8e0",
            "Access to create new roles"
    ),
    ACCESS_MANAGEMENT_ROLE_UPDATE(
            "1bfb0aa1-820d-45ff-b4fb-8fad5f346b0e",
            "Access to update existing roles"
    ),

    ACCESS_MANAGEMENT_PERMISSIONS_VIEW(
            "2a5778d9-dfb7-4d50-893d-9f771d210cd9",
            "Access to permissions page"
    ),

    COMPLETIONS_VIEW(
            "28e90b0c-3076-4b12-b05d-edfc4262331f",
            "Access to completions page"
    ),

    TEMPLATES_VIEW(
            "9a5e5b4c-5f0e-465b-a88a-152fb9791572",
            "Access to templates page"
    ),
    TEMPLATE_CREATE(
            "25664e52-5111-4855-a7da-33e3351a0e17",
            "Access to create new templates"
    ),
    TEMPLATE_UPDATE(
            "531a59a2-e3c6-4022-9d05-68ce5dfa8b9c",
            "Access to update existing templates"
    ),
    TEMPLATE_DELETE(
            "d1f1e8b4-3c6a-4f7e-9f0a-2e3b5f6a7b8d",
            "c69d1710-d42a-4514-8022-2da4a01f6ff7"
    )
    ;

    PermissionType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    private final String key;
    private final String description;

}
