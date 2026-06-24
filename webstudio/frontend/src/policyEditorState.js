export function clearSelectedSourceState(state = {}) {
    return {
        ...state,
        selectedSourceName: "",
        selectedSourceFile: null,
        javaCompileResult: null
    };
}

export function policySourceModalVisible(selectedSourceFile) {
    return selectedSourceFile?.category === "policy";
}
