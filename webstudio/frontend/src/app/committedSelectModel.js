// Keeps select controls visually bound to the last committed value while guarded transitions are pending.
export function committedSelectChangeState(committedValue, requestedValue, normalize = (value) => value) {
    return {
        requestedValue: normalize(requestedValue),
        displayedValue: normalize(committedValue)
    };
}
