import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import B from './b';
import BDetail from './b-detail';
import BUpdate from './b-update';
import BDeleteDialog from './b-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BDetail} />
      <ErrorBoundaryRoute path={match.url} component={B} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BDeleteDialog} />
  </>
);

export default Routes;
