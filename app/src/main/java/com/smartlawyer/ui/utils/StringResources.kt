package com.smartlawyer.ui.utils

import android.content.Context
import androidx.annotation.StringRes

/**
 * Utility class for accessing string resources in a type-safe manner
 */
object StringResources {
    
    // Navigation
    const val BACK = "back"
    const val CANCEL = "cancel"
    const val CONFIRM = "confirm"
    const val SELECT = "select"
    const val SAVE = "save"
    const val RETRY = "retry"
    
    // Dashboard
    const val DASHBOARD_TITLE = "dashboard_title"
    const val DASHBOARD_WELCOME = "dashboard_welcome"
    const val DASHBOARD_DESCRIPTION = "dashboard_description"
    const val VIEW_CLIENTS_LIST = "view_clients_list"
    const val ADD_NEW_CLIENT = "add_new_client"
    const val VIEW_CASES_LIST = "view_cases_list"
    const val REGISTER_NEW_CASE = "register_new_case"
    const val LOGOUT = "logout"
    const val LOGOUT_SUCCESS = "logout_success"
    
    // Authentication
    const val LOGIN_TITLE = "login_title"
    const val REGISTER_TITLE = "register_title"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val CONFIRM_PASSWORD = "confirm_password"
    const val LOGIN_BUTTON = "login_button"
    const val REGISTER_BUTTON = "register_button"
    const val BIOMETRIC_LOGIN = "biometric_login"
    const val NO_ACCOUNT = "no_account"
    const val HAS_ACCOUNT = "has_account"
    const val NORMAL_LOGIN = "normal_login"
    const val SKIP = "skip"
    
    // Client Management
    const val CLIENT_REGISTRATION_TITLE = "client_registration_title"
    const val CLIENT_LIST_TITLE = "client_list_title"
    const val CLIENT = "client"
    const val CLIENT_NAME = "client_name"
    const val CLIENT_ADDRESS = "client_address"
    const val CLIENT_PHONE = "client_phone"
    const val PHONE = "phone"
    const val PHONE_NUMBER = "phone_number"
    const val CLIENT_EMAIL_OPTIONAL = "client_email_optional"
    const val CLIENT_POWER_OF_ATTORNEY_OPTIONAL = "client_power_of_attorney_optional"
    const val POWER_OF_ATTORNEY_NUMBER = "power_of_attorney_number"
    const val POWER_OF_ATTORNEY_FORMAT = "power_of_attorney_format"
    const val SAVE_CLIENT = "save_client"
    const val SEARCH_CLIENTS = "search_clients"
    const val CLIENT_DETAILS = "client_details"
    const val NO_CLIENTS = "no_clients"
    const val NO_CLIENTS_FOUND = "no_clients_found"
    const val NO_SEARCH_RESULTS = "no_search_results"
    const val ADD_CLIENT = "add_client"
    const val DELETE_CLIENT = "delete_client"
    const val DELETE_CLIENT_TITLE = "delete_client_title"
    const val DELETE_CLIENT_CONFIRMATION = "delete_client_confirmation"
    const val CLOSE = "close"
    const val LOADING_CLIENTS = "loading_clients"
    const val ERROR_LOADING_CLIENTS = "error_loading_clients"
    const val ERROR_SEARCHING_CLIENTS = "error_searching_clients"
    
    // Case Management
    const val CASE_REGISTRATION_TITLE = "case_registration_title"
    const val CASE_LIST_TITLE = "case_list_title"
    const val CASE_NUMBER = "case_number"
    const val CASE_YEAR = "case_year"
    const val REGISTRATION_DATE = "registration_date"
    const val SELECT_CLIENT = "select_client"
    const val CLIENT_ROLE = "client_role"
    const val OPPONENT_NAME = "opponent_name"
    const val OPPONENT_ROLE = "opponent_role"
    const val CASE_SUBJECT = "case_subject"
    const val COURT_NAME = "court_name"
    const val CASE_TYPE = "case_type"
    const val FIRST_SESSION_DATE = "first_session_date"
    const val SAVE_CASE = "save_case"
    const val SEARCH_CASES = "search_cases"
    const val CASE_DETAILS = "case_details"
    const val NO_CASES = "no_cases"
    const val ADD_CASE = "add_case"
    
    // File Upload
    const val UPLOAD_FILES_DOCUMENTS = "upload_files_documents"
    const val DOCUMENTS_PDF = "documents_pdf"
    const val IMAGES = "images"
    const val ADD_DOCUMENT = "add_document"
    const val ADD_IMAGE = "add_image"
    const val DOCUMENT = "document"
    const val IMAGE = "image"
    const val OPEN_FILE = "open_file"
    const val FILES_DOCUMENTS = "files_documents"
    const val DOCUMENTS_COUNT = "documents_count"
    const val IMAGES_COUNT = "images_count"
    
    // Validation Messages
    const val ERROR_CASE_NUMBER_REQUIRED = "error_case_number_required"
    const val ERROR_CASE_YEAR_REQUIRED = "error_case_year_required"
    const val ERROR_CASE_YEAR_FORMAT = "error_case_year_format"
    const val ERROR_REGISTRATION_DATE_REQUIRED = "error_registration_date_required"
    const val ERROR_CLIENT_REQUIRED = "error_client_required"
    const val ERROR_CLIENT_ROLE_REQUIRED = "error_client_role_required"
    const val ERROR_OPPONENT_NAME_REQUIRED = "error_opponent_name_required"
    const val ERROR_OPPONENT_ROLE_REQUIRED = "error_opponent_role_required"
    const val ERROR_CASE_SUBJECT_REQUIRED = "error_case_subject_required"
    const val ERROR_COURT_NAME_REQUIRED = "error_court_name_required"
    const val ERROR_FILL_REQUIRED_FIELDS = "error_fill_required_fields"
    const val ERROR_CORRECT_FORM_ERRORS = "error_correct_form_errors"
    const val SUCCESS_CASE_REGISTERED = "success_case_registered"
    
    // Client Validation
    const val ERROR_CLIENT_NAME_REQUIRED = "error_client_name_required"
    const val ERROR_PHONE_REQUIRED = "error_phone_required"
    const val ERROR_PHONE_INVALID = "error_phone_invalid"
    const val ERROR_EMAIL_INVALID = "error_email_invalid"
    const val SUCCESS_CLIENT_REGISTERED = "success_client_registered"
    
    // Authentication Validation
    const val ERROR_EMAIL_REQUIRED = "error_email_required"
    const val ERROR_PASSWORD_REQUIRED = "error_password_required"
    const val ERROR_CONFIRM_PASSWORD_REQUIRED = "error_confirm_password_required"
    const val ERROR_PASSWORDS_NOT_MATCH = "error_passwords_not_match"
    const val ERROR_EMAIL_ALREADY_REGISTERED = "error_email_already_registered"
    const val ERROR_INVALID_EMAIL_FORMAT = "error_invalid_email_format"
    const val ERROR_WEAK_PASSWORD = "error_weak_password"
    const val SUCCESS_ACCOUNT_CREATED = "success_account_created"
    const val ERROR_LOGIN_FAILED = "error_login_failed"
    const val ERROR_INVALID_CREDENTIALS = "error_invalid_credentials"
    
    // Case Types
    const val CASE_TYPE_CIVIL_PARTIAL = "case_type_civil_partial"
    const val CASE_TYPE_CIVIL_FULL = "case_type_civil_full"
    const val CASE_TYPE_LABOR = "case_type_labor"
    const val CASE_TYPE_LABOR_APPEAL = "case_type_labor_appeal"
    const val CASE_TYPE_MISDEMEANOR = "case_type_misdemeanor"
    const val CASE_TYPE_FELONY = "case_type_felony"
    const val CASE_TYPE_ECONOMIC = "case_type_economic"
    const val CASE_TYPE_ADMINISTRATIVE = "case_type_administrative"
    const val CASE_TYPE_HOUSING = "case_type_housing"
    const val CASE_TYPE_CASSATION = "case_type_cassation"
    
    // Biometric Authentication
    const val BIOMETRIC_LOGIN_TITLE = "biometric_login_title"
    const val BIOMETRIC_AUTHENTICATION_TITLE = "biometric_authentication_title"
    const val BIOMETRIC_AUTHENTICATION_SUBTITLE = "biometric_authentication_subtitle"
    const val BIOMETRIC_VERIFYING = "biometric_verifying"
    const val BIOMETRIC_PLACE_FINGER = "biometric_place_finger"
    const val BIOMETRIC_AUTHENTICATION_FAILED = "biometric_authentication_failed"
    const val BIOMETRIC_RETRY_COUNT = "biometric_retry_count"
    const val BIOMETRIC_NORMAL_LOGIN = "biometric_normal_login"
    const val BIOMETRIC_ADD_FINGERPRINT_HELP = "biometric_add_fingerprint_help"
    const val BIOMETRIC_SECURITY_UPDATE_HELP = "biometric_security_update_help"
    
    // Biometric Error Messages
    const val BIOMETRIC_ERROR_NO_ACTIVITY = "biometric_error_no_activity"
    const val BIOMETRIC_ERROR_NO_HARDWARE = "biometric_error_no_hardware"
    const val BIOMETRIC_ERROR_HARDWARE_UNAVAILABLE = "biometric_error_hardware_unavailable"
    const val BIOMETRIC_ERROR_NOT_ENROLLED = "biometric_error_not_enrolled"
    const val BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED = "biometric_error_security_update_required"
    const val BIOMETRIC_ERROR_CHECK_FAILED = "biometric_error_check_failed"
    const val BIOMETRIC_ERROR_UNAVAILABLE = "biometric_error_unavailable"
    const val BIOMETRIC_ERROR_CONTEXT_INVALID = "biometric_error_context_invalid"
    const val BIOMETRIC_ERROR_LOCKOUT = "biometric_error_lockout"
    const val BIOMETRIC_ERROR_LOCKOUT_PERMANENT = "biometric_error_lockout_permanent"
    const val BIOMETRIC_ERROR_CANCELLED = "biometric_error_cancelled"
    const val BIOMETRIC_ERROR_TIMEOUT = "biometric_error_timeout"
    const val BIOMETRIC_ERROR_UNABLE_TO_PROCESS = "biometric_error_unable_to_process"
    const val BIOMETRIC_ERROR_VENDOR = "biometric_error_vendor"
    const val BIOMETRIC_ERROR_GENERAL = "biometric_error_general"
    const val BIOMETRIC_ERROR_PROMPT_FAILED = "biometric_error_prompt_failed"
    const val BIOMETRIC_ERROR_UNEXPECTED = "biometric_error_unexpected"
    const val BIOMETRIC_ERROR_NO_SAVED_DATA = "biometric_error_no_saved_data"
    const val BIOMETRIC_ERROR_LOAD_DATA = "biometric_error_load_data"
    
    // Loading States
    const val LOADING = "loading"
    const val ERROR = "error"
    const val WARNING = "warning"
    
    // Common Actions
    const val ADD = "add"
    const val EDIT = "edit"
    const val DELETE = "delete"
    const val SEARCH = "search"
    const val FILTER = "filter"
    const val REFRESH = "refresh"
    
    // Success Messages
    const val SUCCESS_OPERATION = "success_operation"
    const val SUCCESS_SAVED = "success_saved"
    const val SUCCESS_UPDATED = "success_updated"
    const val SUCCESS_DELETED = "success_deleted"
    
    // Error Messages
    const val ERROR_OPERATION_FAILED = "error_operation_failed"
    const val ERROR_NETWORK = "error_network"
    const val ERROR_SERVER = "error_server"
    const val ERROR_UNKNOWN = "error_unknown"
    const val ERROR_TRY_AGAIN = "error_try_again"
}

/**
 * Extension function to get string resource by key
 */
fun Context.getStringByKey(key: String): String {
    val resourceId = resources.getIdentifier(key, "string", packageName)
    return if (resourceId != 0) {
        getString(resourceId)
    } else {
        // Fallback to key if resource not found
        key
    }
}

/**
 * Extension function to get string resource by key with format arguments
 */
fun Context.getStringByKey(key: String, vararg formatArgs: Any): String {
    val resourceId = resources.getIdentifier(key, "string", packageName)
    return if (resourceId != 0) {
        getString(resourceId, *formatArgs)
    } else {
        // Fallback to key if resource not found
        key
    }
}
