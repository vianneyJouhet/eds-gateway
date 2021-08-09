import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/d">
      <Translate contentKey="global.menu.entities.d" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/a">
      <Translate contentKey="global.menu.entities.a" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/c">
      <Translate contentKey="global.menu.entities.c" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/b">
      <Translate contentKey="global.menu.entities.b" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
