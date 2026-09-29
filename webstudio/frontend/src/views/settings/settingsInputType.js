export function textInputTypeForSetting(setting) {
    return setting?.key === "DataStorePassword" ? "password" : "text";
}
