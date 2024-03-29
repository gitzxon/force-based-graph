<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jspf"%>
<%@ include file="/common/header.jspf"%>
<%@ include file="/common/menu.jspf"%>


<f:view locale="#{facesContext.externalContext.request.locale}">
	<f:loadBundle var="msg" basename="message" />

	<h:form id="mainForm">
		<div id="mainarea">
		<div id="contentarea"><h:commandLink action="userList">
			<h:outputText value="#{msg.userList}" />
		</h:commandLink><br>
		<br>

		<h:outputLabel value="#{msg.userId}" />: <h:outputText
			value="#{user.currentUser.id}" /> <Br>
		<h:outputLabel value="#{msg.username}" />: <h:inputText
			value="#{user.currentUser.username}">
			<f:validateLength minimum="1" />
		</h:inputText> <Br>
		<h:outputLabel value="#{msg.password}" />: <h:inputText
			value="#{user.currentUser.password}">
			<f:validateLength minimum="1" />
		</h:inputText> <Br>

		<h:messages showDetail="true" showSummary="false" /> <c:choose>
			<c:when test="#{msg.userId == null}">
				<h:commandButton value="#{msg.addNewUser}"
					action="#{user.performUser}" />
			</c:when>
			<c:otherwise>
				<h:commandButton value="#{msg.save}" action="#{user.performUser}" />
			</c:otherwise>
		</c:choose></div>
		</div>
	</h:form>

</f:view>

<c:import url="/common/footer.jspf" />