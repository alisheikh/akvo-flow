var get = Ember.get,
    fmt = Ember.String.fmt;

Ember.View.reopen({
    templateForName: function (name, type) {
        'use strict';

        var template;

        if (!name) {
            return;
        }

        try {
            template = require(name);
        } catch (e) {
            throw new Ember.Error(fmt('%@ - Unable to find %@ "%@".', [this, type, name]));
        }

        return template;
    }
});
