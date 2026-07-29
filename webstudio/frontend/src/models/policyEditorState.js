// Implements WS-UX-POLICIES-001: controls focused Java policy source modal visibility.
export function policySourceModalVisible(selectedSourceFile) {
    return selectedSourceFile?.category === "policy";
}
