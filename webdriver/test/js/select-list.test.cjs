const assert = require('node:assert/strict');
const { readFileSync } = require('node:fs');
const path = require('node:path');
const test = require('node:test');
const vm = require('node:vm');

const source = readFileSync(path.join(__dirname, '../../resources/select-list.js'), 'utf8');

function selectField(values = ['saab', 'TESTAR']) {
    class SelectionEvent {
        constructor(type, options) {
            this.type = type;
            this.bubbles = options.bubbles;
        }
    }
    return {
        tagName: 'SELECT', isConnected: true, options: values.map(value => ({ value })), value: values[0], events: [],
        ownerDocument: { defaultView: { Event: SelectionEvent } },
        dispatchEvent(event) { this.events.push(event); }
    };
}

function execute(method, target, value, capturedElement = null, document = {}) {
    const script = vm.runInNewContext('(function () {\n' + source + '\n})', { document });
    return script(target, capturedElement, value, method);
}

for (const method of ['ID', 'NAME', 'CSS']) {
    test('selects a value through ' + method + ' and dispatches bubbling input before change', () => {
        const field = selectField();
        const target = 'shipping > select';
        const document = {
            getElementById: identifier => {
                assert.equal(identifier, target);
                return field;
            },
            getElementsByName: name => {
                assert.equal(name, target);
                return [field];
            },
            querySelectorAll: selector => {
                assert.equal(selector, target);
                return [field];
            }
        };

        assert.equal(execute(method, target, 'TESTAR', null, document), true);
        assert.equal(field.value, 'TESTAR');
        assert.deepEqual(field.events.map(event => [event.type, event.bubbles]), [['input', true], ['change', true]]);
    });
}

test('prefers the captured element over a matching locator for another field', () => {
    const captured = selectField();
    const other = selectField();
    const document = { getElementsByName: () => [other, captured] };

    assert.equal(execute('NAME', 'cars', 'TESTAR', captured, document), true);
    assert.equal(captured.value, 'TESTAR');
    assert.equal(other.value, 'saab');
});

test('supports element-only targeting when selector extraction is ignored or unavailable', () => {
    const captured = selectField();

    assert.equal(execute('ELEMENT', '', 'TESTAR', captured), true);
    assert.equal(captured.value, 'TESTAR');
});

test('uses CSS after the captured element is detached', () => {
    const captured = selectField();
    captured.isConnected = false;
    const replacement = selectField();

    assert.equal(execute('CSS', '#cars', 'TESTAR', captured, { querySelectorAll: () => [replacement] }), true);
    assert.equal(replacement.value, 'TESTAR');
    assert.equal(captured.value, 'saab');
});

test('preserves case-distinct values', () => {
    const field = selectField(['saab', 'Saab']);

    assert.equal(execute('ELEMENT', '', 'Saab', field), true);
    assert.equal(field.value, 'Saab');
    assert.equal(execute('ELEMENT', '', 'saab', field), true);
    assert.equal(field.value, 'saab');
});

test('passes selector and option text as data rather than executing it as JavaScript', () => {
    const value = "O'Reilly\\TESTAR\n\"; throw new Error('injected'); //";
    const target = '[name="O\'Reilly"]';
    const field = selectField([value]);

    assert.equal(execute('CSS', target, value, null, {
        querySelectorAll: selector => {
            assert.equal(selector, target);
            return [field];
        }
    }), true);
    assert.equal(field.value, value);
});

test('returns a useful failure when the target is missing or is not a select', () => {
    assert.match(execute('CSS', '#missing', 'TESTAR', null, { querySelectorAll: () => [] }), /Unable to locate/);
    const field = selectField();
    field.tagName = 'DIV';
    assert.match(execute('ELEMENT', '', 'TESTAR', field), /Unable to locate/);
});

for (const method of ['NAME', 'CSS']) {
    test('rejects ambiguous ' + method + ' locators without changing either field', () => {
        const first = selectField();
        const second = selectField();
        const document = { getElementsByName: () => [first, second], querySelectorAll: () => [first, second] };

        assert.match(execute(method, 'cars', 'TESTAR', null, document), /Multiple elements/);
        assert.equal(first.value, 'saab');
        assert.equal(second.value, 'saab');
        assert.equal(first.events.length, 0);
    });
}

test('reports malformed CSS without changing selection', () => {
    assert.match(execute('CSS', '[', 'TESTAR', null, {
        querySelectorAll: () => { throw new Error('invalid selector'); }
    }), /invalid selector/);
});

test('rejects missing and duplicated option values without clearing the current selection', () => {
    const field = selectField(['saab', 'TESTAR', 'TESTAR']);

    assert.match(execute('ELEMENT', '', 'absent', field), /missing or ambiguous/);
    assert.match(execute('ELEMENT', '', 'TESTAR', field), /missing or ambiguous/);
    assert.equal(field.value, 'saab');
    assert.equal(field.events.length, 0);
});
