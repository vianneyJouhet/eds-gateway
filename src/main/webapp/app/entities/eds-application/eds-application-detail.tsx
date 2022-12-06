import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './eds-application.reducer';

export const EDSApplicationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const eDSApplicationEntity = useAppSelector(state => state.edsgateway.eDSApplication.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="eDSApplicationDetailsHeading">
          <Translate contentKey="edsGatewayApp.eDSApplication.detail.title">EDSApplication</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="edsGatewayApp.eDSApplication.name">Name</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.name}</dd>
          <dt>
            <span id="logo">
              <Translate contentKey="edsGatewayApp.eDSApplication.logo">Logo</Translate>
            </span>
          </dt>
          <dd>
            {eDSApplicationEntity.logo ? (
              <div>
                {eDSApplicationEntity.logoContentType ? (
                  <a onClick={openFile(eDSApplicationEntity.logoContentType, eDSApplicationEntity.logo)}>
                    <img
                      src={`data:${eDSApplicationEntity.logoContentType};base64,${eDSApplicationEntity.logo}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                ) : null}
                <span>
                  {eDSApplicationEntity.logoContentType}, {byteSize(eDSApplicationEntity.logo)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="link">
              <Translate contentKey="edsGatewayApp.eDSApplication.link">Link</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.link}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="edsGatewayApp.eDSApplication.description">Description</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.description}</dd>
          <dt>
            <span id="category">
              <Translate contentKey="edsGatewayApp.eDSApplication.category">Category</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.category}</dd>
          <dt>
            <span id="authorizedRole">
              <Translate contentKey="edsGatewayApp.eDSApplication.authorizedRole">Authorized Role</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.authorizedRole}</dd>
          <dt>
            <span id="needAuth">
              <Translate contentKey="edsGatewayApp.eDSApplication.needAuth">Need Auth</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.needAuth ? 'true' : 'false'}</dd>
          <dt>
            <span id="defaultHidden">
              <Translate contentKey="edsGatewayApp.eDSApplication.defaultHidden">Default Hidden</Translate>
            </span>
          </dt>
          <dd>{eDSApplicationEntity.defaultHidden ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/eds-application" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/eds-application/${eDSApplicationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EDSApplicationDetail;
