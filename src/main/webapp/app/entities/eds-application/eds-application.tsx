import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { openFile, byteSize, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IEDSApplication } from 'app/shared/model/eds-application.model';
import { getEntities } from './eds-application.reducer';

export const EDSApplication = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const eDSApplicationList = useAppSelector(state => state.edsgateway.eDSApplication.entities);
  const loading = useAppSelector(state => state.edsgateway.eDSApplication.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="eds-application-heading" data-cy="EDSApplicationHeading">
        <Translate contentKey="edsGatewayApp.eDSApplication.home.title">EDS Applications</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="edsGatewayApp.eDSApplication.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/eds-application/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="edsGatewayApp.eDSApplication.home.createLabel">Create new EDS Application</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {eDSApplicationList && eDSApplicationList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.logo">Logo</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.link">Link</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.description">Description</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.category">Category</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.authorizedRole">Authorized Role</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.needAuth">Need Auth</Translate>
                </th>
                <th>
                  <Translate contentKey="edsGatewayApp.eDSApplication.defaultHidden">Default Hidden</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {eDSApplicationList.map((eDSApplication, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/eds-application/${eDSApplication.id}`} color="link" size="sm">
                      {eDSApplication.id}
                    </Button>
                  </td>
                  <td>{eDSApplication.name}</td>
                  <td>
                    {eDSApplication.logo ? (
                      <div>
                        {eDSApplication.logoContentType ? (
                          <a onClick={openFile(eDSApplication.logoContentType, eDSApplication.logo)}>
                            <img
                              src={`data:${eDSApplication.logoContentType};base64,${eDSApplication.logo}`}
                              style={{ maxHeight: '30px' }}
                            />
                            &nbsp;
                          </a>
                        ) : null}
                        <span>
                          {eDSApplication.logoContentType}, {byteSize(eDSApplication.logo)}
                        </span>
                      </div>
                    ) : null}
                  </td>
                  <td>{eDSApplication.link}</td>
                  <td>{eDSApplication.description}</td>
                  <td>{eDSApplication.category}</td>
                  <td>{eDSApplication.authorizedRole}</td>
                  <td>{eDSApplication.needAuth ? 'true' : 'false'}</td>
                  <td>{eDSApplication.defaultHidden ? 'true' : 'false'}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/eds-application/${eDSApplication.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/eds-application/${eDSApplication.id}/edit`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/eds-application/${eDSApplication.id}/delete`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="edsGatewayApp.eDSApplication.home.notFound">No EDS Applications found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default EDSApplication;
